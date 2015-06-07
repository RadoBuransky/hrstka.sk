package sk.hrstka.models.domain

import sk.hrstka.models.db

object UserSpec {
  val rado = UserFactory(db.UserSpec.rado)
  val johny = UserFactory(db.UserSpec.johny)
}
