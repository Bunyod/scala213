name := "scala213"
import sbt.Keys._
import sbt._

version in ThisBuild := "0.0.1"

lazy val scala212v = "2.12.8"
lazy val scala213v = "2.13.0"
lazy val supportedScalaVersions = List(scala212v, scala213v)

fork in run := true
// ThisBuild / useCoursier := true

lazy val commonSettings = Seq(
  organization := "com.bunic",
  // crossScalaVersions := supportedScalaVersions,
  scalaVersion := scala213v,
  version := "0.0.1",
  resolvers ++= Seq(
    DefaultMavenRepository,
    Resolver.jcenterRepo,
    Resolver.sbtPluginRepo("releases"),
    Resolver.mavenLocal,
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  ),
  scalacOptions ++= CompilerOptions.cOptions
)

def baseProject(name: String): Project =
  Project(name, file(name))
    .settings(commonSettings: _*)
    .configs(IntegrationTest)
    .settings(Defaults.itSettings)

lazy val `scala213` = (project in file("."))
  .aggregate(`api`, common)

lazy val `api` = baseProject("api")
  .dependsOn(common)
  .settings(
    libraryDependencies ++= Dependencies.api,
    Compile/mainClass := Some("com.bunic.scala213.Boot")
  )


lazy val common = baseProject("common")
  .settings(
    libraryDependencies ++= Dependencies.common
  )

val runServer = inputKey[Unit]("Runs web-server1")

runServer := {
  (run in Compile in `api`).evaluated
}

import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  runAssembly,
  setNextVersion,
  commitNextVersion,
  pushChanges
)

lazy val runAssembly: ReleaseStep = ReleaseStep(
  action = releaseStepCommand("assembly")
)
