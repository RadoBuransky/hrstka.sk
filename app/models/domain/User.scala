package models.domain

import models.domain.Identifiable.Id

case class User(id: Id,
                name: String) extends Identifiable
