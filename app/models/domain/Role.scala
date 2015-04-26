package models.domain

sealed trait Role
case object Eminent extends Role
case object Admin extends Role
