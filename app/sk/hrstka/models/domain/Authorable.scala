package sk.hrstka.models.domain

import sk.hrstka.models.domain.Identifiable.Id

trait Authorable {
  def authorId: Id
}
