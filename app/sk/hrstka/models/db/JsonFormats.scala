package sk.hrstka.models.db

object JsonFormats {
  import play.api.libs.json.Json
  import play.modules.reactivemongo.json.BSONFormats._

  implicit val userFormat = Json.format[User]
  implicit val techFormat = Json.format[Tech]
  implicit val compFormat = Json.format[Comp]
  implicit val voteFormat = Json.format[TechVote]
  implicit val cityFormat = Json.format[City]
}
