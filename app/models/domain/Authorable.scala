package models.domain

import models.domain.Identifiable.Id

trait Authorable {
  def authorId: Id
}
