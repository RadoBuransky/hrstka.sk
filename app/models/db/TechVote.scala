package models.db

import common.HEException
import models.db.Identifiable.Id
import models.db.TechVote._

case class TechVote(_id: Id,
                    authorId: Id,
                    techId: Id,
                    value: Int) extends Identifiable with Authorable {
  if (value != upVoteValue && value != downVoteValue)
    throw new HEException(s"Illegal vote log value! [$value]")
}

object TechVote {
  val upVoteValue = 1
  val downVoteValue = -1
}