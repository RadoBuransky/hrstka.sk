package models.db

import models.db.Identifiable.Id
import org.joda.time.DateTime

trait Authorable {
  self: Identifiable =>
  def authorId: Id
  lazy val created = new DateTime(_id.time)
}
