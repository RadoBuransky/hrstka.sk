import sbt.Tests.Setup
import sbt._

organization := "sk.hrstka"

name := "website"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.6"

lazy val integrationTest = config("it") extend Test

lazy val root = (project in file(".")).
  configs(integrationTest).
  settings(Defaults.itSettings: _*).
  enablePlugins(PlayScala)

scalaSource in integrationTest := baseDirectory.value / "itest" / "scala"

routesGenerator := InjectedRoutesGenerator

libraryDependencies ++= Seq(
  cache,
  "org.webjars"       %   "bootstrap"             % "3.3.2",
  "org.reactivemongo" %%  "play2-reactivemongo"   % "0.10.5.0.akka23.play24-SNAPSHOT",
  "jp.t2v"            %%  "play2-auth"            % "0.13.2",
  "com.github.t3hnar" %%  "scala-bcrypt"          % "2.4",
  "org.scalatest"     %%  "scalatest"             % "2.2.4"             % "it,test",
  "org.mockito"       %   "mockito-all"           % "1.10.19"           % "it,test"
)

ScoverageSbtPlugin.ScoverageKeys.coverageExcludedPackages := "repositories.*"