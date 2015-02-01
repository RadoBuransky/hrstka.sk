package models.db

import common.HEException
import models.db.Identifiable.Id
import org.joda.time.DateTime

trait Authorable {
  self: Identifiable =>
  def author: Id
  def created: DateTime = _id match {
    case Some(id) => new DateTime(id.time)
    case None => throw new HEException("Cannot get timestamp without _id!")
  }
}
