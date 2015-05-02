package models.domain

import models.domain.Identifiable.Id
import reactivemongo.bson.BSONObjectID
import scala.language.implicitConversions

trait Identifiable {
  def id: Id
}

object Identifiable {
  type Id = String

  implicit def toBSON(id: Id): BSONObjectID = BSONObjectID(id)
  implicit def fromBSON(id: BSONObjectID): Id = id.stringify

  def fromHumanName(name: String): Identifiable.Id = {
    // TODO: Handle diacritics, quotes, etc, must be latin alpha string with dashes
    name.toLowerCase.trim.replace(' ', '-')
  }
}
