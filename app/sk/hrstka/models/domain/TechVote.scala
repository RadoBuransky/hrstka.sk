package sk.hrstka.models.domain

import sk.hrstka.models
import sk.hrstka.models.domain.Identifiable.Id

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

object TechVoteFactory {
  def apply(techVote: models.db.TechVote): TechVote =
    new TechVote(
      techId  = techVote.techId.stringify,
      userId  = techVote.userId.stringify,
      value   = techVote.value)
}