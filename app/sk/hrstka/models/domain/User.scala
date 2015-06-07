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
                email: Email,
                role: Role)

case class Email(value: String) {
  if (!value.contains("@"))
    throw new IllegalArgumentException(s"This does not look like an email address! [$value]")
}

object UserFactory {
  def apply(user: models.db.User): User =
    User(
      id    = Identifiable.fromBSON(user._id),
      email = Email(user.email),
      role  = Role(user.role)
    )
}
