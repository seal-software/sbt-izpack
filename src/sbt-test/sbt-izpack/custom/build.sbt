//
// Copyright (c) 2014-2016, MediaCluster GmbH.
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

import com.typesafe.sbt.SbtNativePackager._
import de.mediacluster.sbt._

name := "custom"

organization := "de.mediacluster"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.1"

lazy val simple = project.in(file("."))
  .enablePlugins(SbtIzPack)
  .settings(
    IzPackKeys.configFile := baseDirectory.value / "install.xml",
    artifactName in izpack := { (_,_,_) => "custom.jar" },
    sourceDirectory in izpack := baseDirectory.value / "installer",
    target in izpack := baseDirectory.value / "target"
  )

packageArchetype.java_application
