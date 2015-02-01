package models.domain

import models.domain.Identifiable.Id

trait Identifiable {
  def id: Id
}

object Identifiable {
  type Id = String
}