name := "gocd-hipchat-plugin"

version := "1.0"

scalaVersion := "2.10.4"

val goVersion = "15.1.0"

packageOptions in (Compile, packageBin) +=
  Package.ManifestAttributes( "Go-Version" -> goVersion )

artifactName := { (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
  artifact.name + "." + artifact.extension
}

assemblyJarName in assembly := name.value + ".jar"

scalariformSettings

libraryDependencies ++= Seq(
  "org.scalaj" %% "scalaj-http" % "1.0.1",
  "org.json4s" %% "json4s-native" % "3.2.11" exclude("org.scala-lang", "scalap"),
  "com.thoughtworks.go" %%  "go-plugin-api" % "current"  % "provided" from "https://bintray.com/artifact/download/gocd/gocd/go-plugin-api-15.1.0.jar",
  "org.streum" %% "configrity-core" % "1.0.0" exclude("org.scalatest", "scalatest_2.10")

)

scalacOptions += "-target:jvm-1.7"
