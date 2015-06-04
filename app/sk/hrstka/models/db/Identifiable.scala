package sk.hrstka.models.db

import reactivemongo.bson.BSONObjectID
import sk.hrstka.models.db.Identifiable.Id

trait Identifiable {
  def _id: Id
}

object Identifiable {
  type Id = BSONObjectID
  type Handle = String
  val empty = BSONObjectID(Array[Byte](0,0,0,0,0,0,0,0,0,0,0,0))
  def apply(value: String) = BSONObjectID.apply(value)
}