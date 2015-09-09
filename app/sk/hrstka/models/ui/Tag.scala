package sk.hrstka.models.ui

import sk.hrstka.models.domain

sealed trait Tag {
  def name: String
  def order: Int
}

object HipTag extends Tag {
  override val name = "Hip!"
  override val order = 5
}

object JavaTag extends Tag {
  override val name = "Java"
  override val order = 10
}

object DotNetTag extends Tag {
  override val name = ".NET"
  override val order = 20
}

object CTag extends Tag {
  override val name = "C/C++"
  override val order = 30
}

object PhpTag extends Tag {
  override val name = "PHP"
  override val order = 35
}

object MobileTag extends Tag {
  override val name = "Mobile"
  override val order = 40
}

object WebTag extends Tag {
  override val name = "Web"
  override val order = 50
}

object NoSqlTag extends Tag {
  override val name = "NoSQL"
  override val order = 60
}

object Tag {
  val techHandleToTag: PartialFunction[String, Tag] = {
    case "erlang" => HipTag
    case "amazon-web-services" => HipTag
    case "event-sourcing" => HipTag

    case "java" => JavaTag
    case "scala" => JavaTag

    case "c#" => DotNetTag
    case "asp.net" => DotNetTag
    case ".net" => DotNetTag

    case "c" => CTag
    case "c++" => CTag

    case "php" => PhpTag

    case "android" => MobileTag
    case "ios" => MobileTag
    case "windows-rt" => MobileTag

    case "html" => WebTag
    case "html5" => WebTag
    case "javascript" => WebTag
    case "angularjs" => WebTag

    case "mongodb" => NoSqlTag
    case "couchbase" => NoSqlTag
  }

  def apply(tech: domain.Tech): Option[Tag] =
    techHandleToTag.andThen(Some(_)).applyOrElse(tech.handle.value, (_: String) => None)

  def apply(techs: Seq[domain.TechRating]): Seq[Tag] =
    techs.map(techRating => apply(techRating.tech)).flatten.distinct.toSeq.sortBy(_.order)
}