package sk.hrstka.models.db

import sk.hrstka.models.domain.{Language, Library, Server}

object TechSpec {
  val scala = Tech(
    _id             = Identifiable.empty,
    handle          = "scala",
    categoryHandle  = Language.handle.value,
    name            = "Scala",
    website         = "http://www.scala-lang.org/"
  )

  val java = Tech(
    _id             = Identifiable.empty,
    handle          = "java",
    categoryHandle  = Language.handle.value,
    name            = "Java",
    website         = "https://www.java.com/en/"
  )

  val php = Tech(
    _id             = Identifiable.empty,
    handle          = "php",
    categoryHandle  = Language.handle.value,
    name            = "PHP",
    website         = "http://php.net/"
  )

  val akka = Tech(
    _id             = Identifiable.empty,
    handle          = "akka",
    categoryHandle  = Library.handle.value,
    name            = "Akka",
    website         = "http://akka.io/"
  )

  val apache = Tech(
    _id             = Identifiable.empty,
    handle          = "apache",
    categoryHandle  = Server.handle.value,
    name            = "Apache Tomcat",
    website         = "http://www.apache.org/"
  )
}
