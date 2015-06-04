package sk.hrstka.models.api

object JsonFormats {
  import play.api.libs.json.Json

  implicit val compFormat = Json.format[Comp]
  implicit val techFormat = Json.format[Tech]
}
