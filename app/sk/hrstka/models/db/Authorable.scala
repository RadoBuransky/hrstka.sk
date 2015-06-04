package sk.hrstka.models.db

import sk.hrstka.models.db.Identifiable.Id

trait Authorable {
  def authorId: Id
}
