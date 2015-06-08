package sk.hrstka.models.domain

import reactivemongo.bson.BSONObjectID
import sk.hrstka.models
import sk.hrstka.models.db

import scala.language.implicitConversions

case class Id(value: String)

trait Identifiable {
  def id: Id
}

object Identifiable {
  val empty: Id = Id("")
  implicit def toBSON(id: Id): BSONObjectID = {
    if (id == empty)
      db.Identifiable.empty
    else
      BSONObjectID(id.value)
  }
  implicit def toDb(handle: Handle): models.db.Identifiable.Handle = handle.value
  implicit def fromBSON(id: BSONObjectID): Id = {
    if (id == db.Identifiable.empty)
      empty
    else
      Id(id.stringify)
  }
}
