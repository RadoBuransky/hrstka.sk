package models.db

import models.db.Identifiable.Id

sealed trait Event extends Identifiable with Authorable

case class TechVoteEvent(_id: Option[Id],
                         author: Id,
                         value: Int) extends Event