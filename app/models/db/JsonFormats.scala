package models.db

object JsonFormats {
  import play.api.libs.json.Json
  import play.modules.reactivemongo.json.BSONFormats._

  implicit val techFormat = Json.format[Tech]
  implicit val compFormat = Json.format[Comp]
  implicit val compTechFormat = Json.format[CompTech]
  implicit val voteFormat = Json.format[Vote]
  implicit val voteLogFormat = Json.format[VoteLog]
}
