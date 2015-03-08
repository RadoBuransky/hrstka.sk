package models.domain

import models.db.Vote
import models.domain.Identifiable.Id

case class TechVote(techId: Id,
                    authorId: Id,
                    value: Int) extends Authorable

object TechVote {
  def apply(vote: Vote): TechVote = new TechVote(vote.id.stringify, vote.authorId.stringify, vote.value)
}