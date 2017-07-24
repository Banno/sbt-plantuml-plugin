name := "sbt-plantuml-plugin"

organization := "com.banno"

version := "1.1.3-SNAPSHOT"

scalaVersion := "2.10.5"

sbtPlugin := true

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")

// bintray
resolvers ++= Seq(
  "Scalaz Bintray Repo" at "https://dl.bintray.com/scalaz/releases"
)

publishTo := ((version) { v =>
  if (v.trim.endsWith("SNAPSHOT")) {
    Some("Banno Snapshots Repo" at "http://nexus.banno.com/nexus/content/repositories/snapshots")
  } else {
    Some("Banno Releases Repo" at "http://nexus.banno.com/nexus/content/repositories/releases")
  }
}).value

credentials += Credentials(Path.userHome / ".ivy2" / ".banno_credentials")

// more bintray
bintrayOrganization := Some("banno")

bintrayRepository := "oss"

bintrayPackageLabels := Seq("sbt")

licenses ++= Seq(("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.html")))

// specs2 support
libraryDependencies ++= Seq(
  "commons-io" % "commons-io" % "2.5",
  "org.specs2" %% "specs2-core" % "3.9.4" % "test"
)

// sbt scripted
ScriptedPlugin.scriptedSettings

scriptedBufferLog := false

scriptedLaunchOpts += (version { "-Dplugin.version=" + _ }).value

// don't publish sources or scaladoc jars
publishArtifact in (Compile, packageSrc) := false

publishArtifact in (Compile, packageDoc) := false

libraryDependencies += "net.sourceforge.plantuml" % "plantuml" % "1.2017.15"
