package sk.hrstka.models.domain

import sk.hrstka.models

/**
 * Common interface for vote entities.
 */
sealed trait Vote {
  def entityId: Id
  def userId: Id
  def value: Int
}

/**
 * Technology vote value of an user.
 *
 * @param entityId Technology identifier.
 * @param userId User identifier.
 * @param value Vote value.
 */
case class TechVote(entityId: Id,
                    userId: Id,
                    value: Int) extends Vote

/**
 * Company vote value of an user.
 *
 * @param entityId Company identifier.
 * @param userId User identifier.
 * @param value Vote value.
 */
case class CompVote(entityId: Id,
                    userId: Id,
                    value: Int) extends Vote

object VoteFactory {
  def apply(techVote: models.db.TechVote): TechVote = apply(techVote, TechVote.apply)
  def apply(compVote: models.db.CompVote): CompVote = apply(compVote, CompVote.apply)

  private def apply[T <: Vote](vote: models.db.Vote, factory: (Id, Id, Int) => T): T = factory(
    Identifiable.fromBSON(vote.entityId),
    Identifiable.fromBSON(vote.userId),
    vote.value)
}