package models.domain

sealed trait Role {
  def name: String
}
case object Visitor extends Role {
  val name = Role.eminentName
}
case object Eminent extends Role {
  val name = Role.eminentName
}
case object Admin extends Role {
  val name = Role.adminName
}

object Role {
  val eminentName = "eminent"
  val adminName = "admin"
  val visitorName = "visitor"

  def apply(role: String) = role match {
    case `eminentName` => Eminent
    case `adminName` => Admin
    case _ => Visitor
  }
}