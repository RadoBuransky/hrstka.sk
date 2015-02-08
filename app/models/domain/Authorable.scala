package models.domain

import models.domain.Identifiable.Id

trait Authorable {
  self: Identifiable =>
  def authorId: Id
}
