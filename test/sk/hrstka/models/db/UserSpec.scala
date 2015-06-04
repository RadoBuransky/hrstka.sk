package sk.hrstka.models.db

object UserSpec {
  val radoPassword = "abc"
  val rado = User(
    _id               = Identifiable.empty,
    email             = "radoburansky@gmail.com",
    encryptedPassword = "$2a$10$GhhDT6A3pNjFdyK24eqXzODE7rvjROhWe9gpGK.QUOO9j7gvsdme2",
    role              = "slave"
  )

  val johny = User(
    _id               = Identifiable.empty,
    email             = "johny@noidea.com",
    encryptedPassword = "xxx",
    role              = "master"
  )
}
