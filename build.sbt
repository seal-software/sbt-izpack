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
val SealIzpackVersion = "5.1.3-SEAL.6"

sbtPlugin := true

name := "sbt-izpack"

organization := "de.mediacluster.sbt"

scalaVersion := "2.12.8"

publishMavenStyle := true
publishTo := Some("releases" at "http://nexus.seal-software.net/content/repositories/releases/")

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
  "org.codehaus.izpack" % "izpack-core" % SealIzpackVersion,
  "org.codehaus.izpack" % "izpack-gui" % SealIzpackVersion,
  "org.codehaus.izpack" % "izpack-uninstaller" % SealIzpackVersion,
  "org.codehaus.izpack" % "izpack-panel" % SealIzpackVersion ,
  "org.codehaus.izpack" % "izpack-installer" % SealIzpackVersion,
  "org.codehaus.izpack" % "izpack-event" % SealIzpackVersion,
  "org.codehaus.izpack" % "izpack-api" % SealIzpackVersion,
  "org.codehaus.izpack" % "izpack-util" % SealIzpackVersion,
  "org.codehaus.izpack" % "izpack-wrapper" % SealIzpackVersion,
  "org.codehaus.izpack" % "izpack-tools" % SealIzpackVersion,
  "org.codehaus.izpack" % "izpack-ant" % SealIzpackVersion,
  "org.codehaus.izpack" % "izpack-compiler" % SealIzpackVersion
)

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.2.2")

enablePlugins(GitBranchPrompt)

//bintrayPackageLabels := Seq("sbt", "izpack")

//bintrayOrganization := Some("mediacluster")

//bintrayVcsUrl := Some("git@github.com:MediaCluster/sbt-izpack.git")

licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))

publishMavenStyle := true

//scriptedSettings

/*scriptedLaunchOpts ++= Seq(
  s"-Dproject.version=${version.value}"
)

scriptedBufferLog := false
*/

