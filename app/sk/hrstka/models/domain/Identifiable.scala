package sk.hrstka.models.domain

import reactivemongo.bson.BSONObjectID
import sk.hrstka.models

import scala.language.implicitConversions

case class Id(value: String)

trait Identifiable {
  def id: Id
}

object Identifiable {
  val empty: Id = Id("")
  implicit def toBSON(id: Id): BSONObjectID = BSONObjectID(id.value)
  implicit def toBSON(handle: Handle): models.db.Identifiable.Handle = handle.value
  implicit def fromBSON(id: BSONObjectID): Id = Id(id.stringify)
}
