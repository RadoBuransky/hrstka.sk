package models.db

case class City(_id: Identifiable.Id,
                handle: String,
                sk: String) extends Identifiable
