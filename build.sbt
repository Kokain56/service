import sbt.Keys.libraryDependencies

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.14"

lazy val Http4sVersion = "0.23.18"
lazy val CirceVersion = "0.14.2"

lazy val http4s = Seq[ModuleID](
  "org.http4s" %% "http4s-client" % Http4sVersion,
  "org.http4s" %% "http4s-dsl" % Http4sVersion,
  "org.http4s" %% "http4s-ember-server" % Http4sVersion,
  "org.http4s" %% "http4s-ember-client" % Http4sVersion
)

lazy val cats = Seq[ModuleID](
  "org.typelevel" %% "cats-core" % "2.10.0",
  "org.typelevel" %% "cats-effect" % "3.4.5",
  "org.typelevel" %% "cats-effect-testing-scalatest" % "1.5.0" % Test
)

lazy val circe = Seq(
  "io.circe" %% "circe-core" % CirceVersion,
  "io.circe" %% "circe-generic" % CirceVersion,
  "io.circe" %% "circe-parser" % CirceVersion,
  "io.circe" %% "circe-derivation" % "0.13.0-M4",
  "org.http4s" %% "http4s-circe" % "0.23.14"
)

lazy val fs2: Seq[ModuleID] = Seq(
  "co.fs2" %% "fs2-core" % "3.6.1",
  "co.fs2" %% "fs2-io"   % "3.6.1"
)

lazy val root = (project in file("."))
  .settings(
    name := "untitled2",
    libraryDependencies ++= http4s,
    libraryDependencies ++= cats,
    libraryDependencies ++= circe,
    libraryDependencies ++= fs2,
    libraryDependencies += "org.scalatestplus" %% "mockito-5-10" % "3.2.18.0" % Test
  )
