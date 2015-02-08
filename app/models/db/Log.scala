package models.db

import common.HEException
import models.db.Identifiable.Id
import TechVoteLog._

sealed trait Log extends Identifiable with Authorable

case class TechVoteLog(_id: Id,
                       authorId: Id,
                       techId: Id,
                       value: Int) extends Log {
  if (value != upVoteValue && value != downVoteValue)
    throw new HEException(s"Illegal vote log value! [$value]")
}

object TechVoteLog {
  val upVoteValue = 1
  val downVoteValue = -1
}