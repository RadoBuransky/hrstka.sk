package models.api

object JsonFormats {
  import play.api.libs.json.Json
  import play.modules.reactivemongo.json.BSONFormats._

  implicit val compFormat = Json.format[models.api.Comp]
  implicit val techFormat = Json.format[models.api.Tech]
}
