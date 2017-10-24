//
// Copyright (c) 2014-2016, MediaCluster GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

sbtPlugin := true

name := "sbt-izpack"

organization := "de.mediacluster.sbt"

scalaVersion := "2.10.5"

scalacOptions in Compile ++= Seq(
  "-encoding", "UTF-8",
  "-target:jvm-1.7",
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xlog-reflective-calls",
  "-Xlint"
)

javacOptions in (Compile, compile) ++= Seq(
  "-source", "1.7",
  "-target", "1.7",
  //"-Werror",
  "-Xlint:all",
  "-Xlint:-options",
  "-Xlint:-path",
  "-Xlint:unchecked",
  "-Xlint:deprecation"
)

resolvers ++= Seq(
  "Seal Repo" at "http://nexus.seal-software.net/content/groups/public/",
  "IceSoft" at "http://anonsvn.icesoft.org/repo/maven2/releases/",
  "JBoss" at "https://repository.jboss.org/nexus/content/repositories/thirdparty-releases/"
)

libraryDependencies ++= Seq(
  "com.novocode" % "junit-interface" % "0.11" % "test",
  "junit" % "junit" % "4.12" % "test",
  "org.codehaus.izpack" % "izpack-core" % "5.1.2-SEAL.13",
  "org.codehaus.izpack" % "izpack-gui" % "5.1.2-SEAL.13",
  "org.codehaus.izpack" % "izpack-uninstaller" % "5.1.2-SEAL.13",
  "org.codehaus.izpack" % "izpack-panel" % "5.1.2-SEAL.13" ,
  "org.codehaus.izpack" % "izpack-installer" % "5.1.2-SEAL.13",
  "org.codehaus.izpack" % "izpack-event" % "5.1.2-SEAL.13",
  "org.codehaus.izpack" % "izpack-api" % "5.1.2-SEAL.13",
  "org.codehaus.izpack" % "izpack-util" % "5.1.2-SEAL.13",
  "org.codehaus.izpack" % "izpack-wrapper" % "5.1.2-SEAL.13",
  "org.codehaus.izpack" % "izpack-tools" % "5.1.2-SEAL.13",
  "org.codehaus.izpack" % "izpack-ant" % "5.1.2-SEAL.13",
  "org.codehaus.izpack" % "izpack-compiler" % "5.1.2-SEAL.13"
)

TaskKey[Unit]("git-list-tags") := {
  val cmd = git.runner.value
  val cwd = baseDirectory.value
  val log = streams.value.log
  println("Base directory is " + cwd)
  cmd.apply("tag","-l")(cwd, log)
}

addSbtPlugin("com.typesafe.sbt" %% "sbt-native-packager" % "1.1.5")

enablePlugins(GitBranchPrompt)

bintrayPackageLabels := Seq("sbt", "izpack")

bintrayOrganization := Some("mediacluster")

bintrayVcsUrl := Some("git@github.com:MediaCluster/sbt-izpack.git")

licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))

publishMavenStyle := false

pomExtra := <url>https://github.com/MediaCluster/sbt-izpack</url>
  <licenses>
    <license>
      <name>Apache 2</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:MediaCluster/sbt-izpack.git</url>
    <connection>scm:git:git@github.com:MediaCluster/sbt-izpack.git</connection>
  </scm>
  <developers>
    <developer>
      <id>maichler</id>
      <name>Michael Aichler</name>
      <url>http://www.mediacluster.de</url>
    </developer>
  </developers>

scriptedSettings

scriptedLaunchOpts <+= version apply { v => s"-Dproject.version=$v" }

scriptedBufferLog := false
