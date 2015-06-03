package models.domain

import models.db
import models.domain.Identifiable.Id

/**
 * Technology vote value of an user.
 *
 * @param techId Technology identifier.
 * @param userId User identifier.
 * @param value Vote value.
 */
case class TechVote(techId: Id,
                    userId: Id,
                    value: Int)

object TechVote {
  def apply(techVote: db.TechVote): TechVote =
    new TechVote(
      techId  = techVote.techId.stringify,
      userId  = techVote.userId.stringify,
      value   = techVote.value)
}