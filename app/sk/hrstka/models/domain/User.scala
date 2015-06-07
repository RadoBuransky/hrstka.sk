package sk.hrstka.models.domain

import sk.hrstka.models

/**
 * User of this application.
 *
 * @param id Unique identifier.
 * @param email User email address, also unique.
 * @param role User role.
 */
case class User(id: Id,
                email: String,
                role: Role)

object UserFactory {
  def apply(user: models.db.User): User =
    User(
      id    = Identifiable.fromBSON(user._id),
      email = user.email,
      role  = Role(user.role)
    )
}
