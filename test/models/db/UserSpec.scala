package models.db

object UserSpec {
  val rado = User(
    _id               = Identifiable.empty,
    email             = "radoburansky@gmail.com",
    encryptedPassword = "abc",
    role              = "slave"
  )

  val johny = User(
    _id               = Identifiable.empty,
    email             = "johny@noidea.com",
    encryptedPassword = "xxx",
    role              = "master"
  )
}
