package models.db

import models.db.Identifiable.Id

trait Authorable {
  def authorId: Id
}
