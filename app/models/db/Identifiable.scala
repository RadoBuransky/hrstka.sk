package models.db

import models.db.Identifiable.Id
import reactivemongo.bson.BSONObjectID

trait Identifiable {
  def _id: Id
}

object Identifiable {
  type Id = BSONObjectID
  def apply(value: String) = BSONObjectID.apply(value)
}