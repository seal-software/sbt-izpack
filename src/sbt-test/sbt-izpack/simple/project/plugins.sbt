addSbtPlugin("de.mediacluster.sbt" % "sbt-izpack" % sys.props("project.version"))

addSbtPlugin("com.typesafe.sbt" %% "sbt-native-packager" % "0.7.4")

resolvers ++= Seq(
  Resolver.mavenLocal,
  "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"
)
