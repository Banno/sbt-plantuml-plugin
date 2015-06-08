import bintray.Keys._

name := "sbt-plantuml-plugin"

organization := "com.banno"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.6"

crossScalaVersions := Seq("2.10.5", "2.11.6")

// javacOptions ++= Seq("-Xlint:deprecation", "-Xlint:unchecked")

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")

resolvers ++= Seq(
  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases",
  "spray.io" at "http://repo.spray.io/",
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"
)

publishTo <<= (version) { v =>
  if (v.trim.endsWith("SNAPSHOT")) {
    Some("Banno Snapshots Repo" at "http://nexus.banno.com/nexus/content/repositories/snapshots")
  } else {
    Some("Banno Releases Repo" at "http://nexus.banno.com/nexus/content/repositories/releases")
  }
}

credentials += Credentials(Path.userHome / ".ivy2" / ".banno_credentials")

// bintray
bintrayPublishSettings

bintrayOrganization in bintray := Some("com.banno")

repository in bintray := "oss"

licenses ++= Seq(("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.html")))

// specs2 support
libraryDependencies ++= Seq(
  "org.specs2" %% "specs2-core" % "3.6.1" % "test"
)
