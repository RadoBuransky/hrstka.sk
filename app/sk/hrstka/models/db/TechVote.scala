package sk.hrstka.models.db

import sk.hrstka.models.db.Identifiable.Id

/**
 * Technology vote value of an user.
 *
 * @param _id Technology vote identifier.
 * @param techId Technology identifier.
 * @param userId User identifier.
 * @param value Vote value.
 */
case class TechVote(_id: Id,
                    techId: Id,
                    userId: Id,
                    value: Int) extends Identifiable