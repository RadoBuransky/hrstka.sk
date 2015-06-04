package sk.hrstka.models.domain

import reactivemongo.bson.BSONObjectID
import sk.hrstka.models
import sk.hrstka.models.domain.Identifiable.Id

import scala.language.implicitConversions

trait Identifiable {
  def id: Id
}

object Identifiable {
  type Id = String
  val empty: Id = ""

  implicit def toBSON(id: Id): BSONObjectID = BSONObjectID(id)
  implicit def toBSON(handle: Handle): models.db.Identifiable.Handle = handle.value
  implicit def fromBSON(id: BSONObjectID): Id = id.stringify
}
