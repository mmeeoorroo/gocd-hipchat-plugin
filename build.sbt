name := "gocd-hipchat-plugin"

version := "1.0"

scalaVersion := "2.10.4"

val goVersion = "14.1.0"

packageOptions in (Compile, packageBin) +=
  Package.ManifestAttributes( "Go-Version" -> goVersion )

artifactName := { (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
  artifact.name + "." + artifact.extension
}

assemblyJarName in assembly := name.value + ".jar"

libraryDependencies += "com.thoughtworks.go" %  "go-plugin-api" % "current"  % "provided" from "http://www.thoughtworks.com/products/docs/go/current/help/resources/go-plugin-api-current.jar"

scalariformSettings

libraryDependencies ++=  Seq(
  "org.scalaj" %% "scalaj-http" % "1.0.1",
  "org.json4s" %% "json4s-native" % "3.2.11" exclude("org.scala-lang", "scalap"))

scalacOptions += "-target:jvm-1.6"

libraryDependencies += "org.specs2" %% "specs2" % "2.4.2" % "test"