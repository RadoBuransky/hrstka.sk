package models.db

object JsonFormats {
  import play.api.libs.json.Json
  import play.modules.reactivemongo.json.BSONFormats._

  implicit val techFormat = Json.format[Tech]
  implicit val techVoteLogFormat = Json.format[TechVoteLog]
}
