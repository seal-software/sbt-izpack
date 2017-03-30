/*
 * sbt-izpack
 *
 * Copyright (c) 2014-2016, MediaCluster GmbH.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mediacluster.sbt

import java.io.{File, FileNotFoundException}
import java.nio.file.{Files, Path, Paths}
import java.util
import java.util.logging.LogRecord

import com.izforge.izpack.sbt.IzPackSbtMojo
import com.typesafe.sbt.packager.universal.UniversalPlugin
import sbt.Keys._
import sbt._

object Import {

  val izpack = TaskKey[Unit]("izpack", "Generate an IzPack installer jar.")

  object IzPackKeys {

    val izpackHome = settingKey[File]("izpack-home")
    val compressionFormat = SettingKey[String]("izpack-compression-format", "Defines the compression format for packs.")
    val compressionLevel = SettingKey[Int]("izpack-compression-level", "Defines the compression level, if supported.")
    val configFile = SettingKey[File]("izpack-config-file", "The XML file describing the installation.")
    val kind = SettingKey[String]("izpack-kind", "Defines the kind of installer to generate.")
    val outputName = TaskKey[String]("izpack-output-name", "The generated name of the installer file.")
    val outputFile = TaskKey[File]("izpack-output-file", "The generated path to installer file.")
    val variables = TaskKey[Map[String,String]]("izpack-variables")
  }
}

/**
 * Simple wrapper around IzPack with some custom panels and actions.
 *
 * @author Michael Aichler (maichler@gmail.com)
 */
object SbtIzPack extends AutoPlugin {

  override def requires = UniversalPlugin

  val autoImport = Import

  import autoImport._
  import autoImport.IzPackKeys._
  import UniversalPlugin.autoImport._

  override def projectSettings = Seq(

    izpack := createIzPackTask.dependsOn(stage in Universal).value,

    izpackHome := target.value / "izpack",
    configFile := (sourceDirectory in izpack).value / "install.xml",
    outputName := generateArtifactName.value,
    outputFile := (target in izpack).value / outputName.value,
    kind := "standard",
    compressionFormat := "default",
    compressionLevel := -1,

    variables := Seq(
      "appName" -> name.value,
      "appVersion" -> version.value,
      "artifactPath" -> outputFile.value.getAbsolutePath,
      "baseDirectory" -> baseDirectory.value.absolutePath,
      "normalizedAppName" -> normalizedName.value,
      "scalaVersion" -> scalaVersion.value,
      "stagingDirectory" -> (stagingDirectory in izpack).value.getAbsolutePath
    ).toMap[String,String]

  ) ++ inTask(izpack)(
    Seq(
      artifactName := {
        (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
          "%s_%s-%s-installer.%s".format(artifact.name, sv.full, module.revision, artifact.extension)
      },
      sourceDirectory := sourceDirectory.value / "izpack",
      stagingDirectory := (stagingDirectory in Universal).value,
      target := target.value / "izpack",
      logLevel := Level.Info
    )
  )

  private def createIzPackTask = {
    (configFile, sourceDirectory in izpack, outputFile, variables, kind, compressionFormat,
      compressionLevel, izpackHome, streams) map {
      (configFile, sourceDirectory, targetFile, variables, kind, format, level, home, streams) =>

        val binDir = new File(home, "bin").getAbsolutePath

        createDirectory(binDir, "customActions")
        createDirectory(binDir, "panels")

        val jarFile = extractIzPackJarLocation
        streams.log.info("SbtIzPack plugin located at " + jarFile)

        createOrReplaceSymlink(jarFile, Paths.get(binDir, "customActions", "CustomListener.jar"))
        createOrReplaceSymlink(jarFile, Paths.get(binDir, "panels", "LoadPropertiesPanel.jar"))

        executeIzPackTask(configFile, sourceDirectory, targetFile, home, kind, format, level, variables, streams.log)
    }
  }

  private def executeIzPackTask(configFile:File, sourceDirectory:File, targetFile:File,
                                homeDirectory:File, kind:String, format:String, level:Int,
                                variables:Map[String,String], log:Logger) = {

    val filename = configFile.getAbsolutePath
    val basedir = sourceDirectory.getAbsolutePath
    val output = targetFile.getAbsolutePath

    log.info("-> Processing : " + filename)
    log.info("-> Output : " + output)
    log.info("-> Base path : " + variables("stagingDirectory"))
    log.info("-> Kind : " + kind)
    log.info("-> Compression : " + format)
    log.info("-> Compr. level : " + level)
    log.info("-> IzPack home : " + homeDirectory)
    log.info("Applying predefined variables " + variables)

    val mojo = new IzPackSbtMojo()
    mojo.baseDir = basedir
    mojo.comprFormat = format
    mojo.comprLevel = level
    mojo.installFile = filename
    mojo.kind = kind
    mojo.mkdirs = true
    mojo.output = output
    mojo.handler = new CompilerListener(log)

    for ((key, value) <- variables) {
      mojo.properties.setProperty(key, value)
    }

    mojo.execute()

    log.info("Installer created in " + targetFile.getParent)
  }

  private def generateArtifactName = {
    (projectID, artifact, scalaVersion in artifactName, scalaBinaryVersion in artifactName, artifactName in izpack) map {
      (module, a, sv, sbv, toString) =>
        toString(ScalaVersion(sv, sbv), module, a)
    }
  }

  /**
    * Extracts the path to the JAR archive of this plugin.
    *
    * @return The path to the JAR archive of this plugin.
    */
  private def extractIzPackJarLocation = {

    val resourceName = String.format("/%s.class", getClass.getName.replace('.', '/'))
    val resourcePath = getClass.getResource(resourceName).getPath
    val endPos = resourcePath.lastIndexOf('!')

    if (-1 == endPos)
      throw new IllegalStateException("Extracting IzPack archive location failed")

    val jarFile = new File(uri(url(resourcePath.substring(0, endPos)).toExternalForm))
    if (!jarFile.exists())
      throw new FileNotFoundException(jarFile.getPath)

    jarFile.getAbsolutePath
  }

  /**
   * Create symlink or replace an existing symlink with the same name.
   *
   * @param source the path to where the link should point to
   * @param link the link path
   */
  private def createOrReplaceSymlink(source:String, link:Path) = {

      Files.deleteIfExists(link)
      Files.createSymbolicLink(link, Paths.get(source))
  }

  /**
   * Create directory including its parents, if necessary.
   *
   * @param parent the parent directory path
   * @param child the name of the directory to be created
   */
  private def createDirectory(parent:String,child:String) = {

      Files.createDirectories(Paths.get(parent,child))
  }

  class CompilerListener(log:Logger) extends java.util.logging.Handler {

    override def flush(): Unit = {

    }

    override def publish(record: LogRecord): Unit = {

      record.getLevel match {
        case util.logging.Level.ALL => log.verbose(record.getMessage)
        case util.logging.Level.CONFIG => log.verbose(record.getMessage)
        case util.logging.Level.FINE => log.verbose(record.getMessage)
        case util.logging.Level.FINER => log.verbose(record.getMessage)
        case util.logging.Level.FINEST => log.verbose(record.getMessage)
        case util.logging.Level.INFO => log.info(record.getMessage)
        case util.logging.Level.OFF => log.debug(record.getMessage)
        case util.logging.Level.SEVERE => log.error(record.getMessage)
        case util.logging.Level.WARNING => log.warn(record.getMessage)
      }
    }

    override def close(): Unit = {

    }
  }
}