package sk.hrstka.models.domain

sealed trait Role {
  self: Role =>
  def name: String
  def isA(role: Role): Boolean = role == this
}
object Visitor extends Role {
  override val name = Role.eminentName
}
object Eminent extends Role {
  override val name = Role.eminentName
  override def isA(role: Role) = Visitor.isA(role) || role == this
}
object Admin extends Role {
  override val name = Role.adminName
  override def isA(role: Role) = Eminent.isA(role) || role == this
}

object Role {
  val eminentName = "eminent"
  val adminName = "admin"
  val visitorName = "visitor"

  def apply(role: String): Role = role match {
    case `eminentName` => Eminent
    case `adminName` => Admin
    case _ => Visitor
  }
}