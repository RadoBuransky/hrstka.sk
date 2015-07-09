import sbt._

organization := "sk.hrstka"

name := "website"

version := "1.1.0-SNAPSHOT"

scalaVersion := "2.11.7"

lazy val integrationTest = config("it") extend Test

lazy val root = (project in file(".")).
  configs(integrationTest).
  settings(Defaults.itSettings: _*).
  enablePlugins(PlayScala)

scalaSource in integrationTest := baseDirectory.value / "itest" / "scala"

routesGenerator := InjectedRoutesGenerator

libraryDependencies ++= Seq(
  cache,
  ws,
  "org.reactivemongo"   %%  "play2-reactivemongo"   % "0.11.0.play24",
  "jp.t2v"              %%  "play2-auth"            % "0.13.2",
  "com.github.t3hnar"   %%  "scala-bcrypt"          % "2.4",
  "com.github.rjeschke" %   "txtmark"               % "0.13",
  "com.typesafe.play"   %%  "play-test"             % "2.4.2"             % "it",
  "org.scalatest"       %%  "scalatest"             % "2.2.4"             % "it,test",
  "org.mockito"         %   "mockito-all"           % "1.10.19"           % "it,test",
  "org.scalatestplus"   %%  "play"                  % "1.4.0-M3"          % "it,test",
  "jp.t2v"              %%  "play2-auth-test"       % "0.13.2"            % "test"
)

ScoverageSbtPlugin.ScoverageKeys.coverageExcludedPackages :=
  "controllers.*;" +
  ".*Reverse.*Controller;" +
  "router.*;" +
  "sk.hrstka.repositories.*;" +
  "sk.hrstka.common.impl.EhHrstkaCache.*;" +
  "sk.hrstka.controllers.auth.impl.*;" +
  "sk.hrstka.views.html.auth.*"

org.scoverage.coveralls.Imports.CoverallsKeys.coverallsToken := Some("1GNicvuJbISxJHIXvjsm4wg5ZZBCJ0lRU")