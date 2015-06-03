package models.domain

import models.db

/**
 * User of this application.
 *
 * @param id Unique identifier.
 * @param email User email address, also unique.
 * @param role User role.
 */
case class User(id: Identifiable.Id,
                email: String,
                role: Role)

object User {
  def apply(user: db.User): User =
    User(
      id    = user._id.stringify,
      email = user.email,
      role  = Role(user.role)
    )
}
