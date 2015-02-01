package models.db

import models.db.Identifiable.Id
import reactivemongo.bson.BSONObjectID

trait Identifiable {
  def _id: Option[Id]
}

object Identifiable {
  type Id = BSONObjectID
}