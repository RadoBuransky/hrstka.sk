resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"

resolvers += Classpaths.sbtPluginReleases

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.4.2")

// web plugins
addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.0.0")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.0.4")

addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.0.0")
