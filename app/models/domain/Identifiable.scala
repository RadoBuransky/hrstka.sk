package models.domain

import models.domain.Identifiable.Id

trait Identifiable {
  def id: Option[Id]
}

object Identifiable {
  type Id = String
}