package models.domain

import models.db

case class User(email: String, role: Role)

object User {
  def apply(user: db.User): User = User(user.email, Role(user.role))
}
