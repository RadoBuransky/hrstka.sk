package models.db

case class City(_id: Identifiable.Id,
                normalizedName: String,
                humanName: String) extends Identifiable
