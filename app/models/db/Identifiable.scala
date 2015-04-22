package models.db

import models.db.Identifiable.Id
import reactivemongo.bson.{BSONValue, BSONObjectID}

trait Identifiable {
  def _id: Id
}

object Identifiable {
  type Id = BSONObjectID
  val empty = BSONObjectID(Array[Byte](0,0,0,0,0,0,0,0,0,0,0,0))
  def apply(value: String) = BSONObjectID.apply(value)
}