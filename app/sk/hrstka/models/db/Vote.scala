package sk.hrstka.models.db

import sk.hrstka.models.db.Identifiable.Id

/**
 * Common trait for vote entities.
 */
sealed trait Vote extends Identifiable {
  def entityId: Id
  def userId: Id
  def value: Int
}

/**
 * Technology vote value of an user.
 *
 * @param _id Technology vote identifier.
 * @param entityId Technology identifier.
 * @param userId User identifier.
 * @param value Vote value.
 */
case class TechVote(_id: Id,
                    entityId: Id,
                    userId: Id,
                    value: Int) extends Vote

/**
 * Company vote value of an user.
 *
 * @param _id Company vote identifier.
 * @param entityId Company identifier.
 * @param userId User identifier.
 * @param value Vote value.
 */
case class CompVote(_id: Id,
                    entityId: Id,
                    userId: Id,
                    value: Int) extends Vote