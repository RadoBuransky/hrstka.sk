name := """hrstka-eminencie"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.5"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "org.webjars" % "bootstrap" % "3.3.2",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.5.0.akka23"
)
