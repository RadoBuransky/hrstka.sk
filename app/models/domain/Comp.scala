package models.domain

import java.net.URL

import models.db
import models.domain.Identifiable.Id

case class Comp(id: Id,
                name: String,
                website: URL,
                location: String,
                employeeCount: Option[Int],
                codersCount: Option[Int],
                femaleCodersCount: Option[Int],
                note: String,
                techs: Seq[Tech],
                joel: Set[Int]) extends Identifiable

object Comp {
  def apply(comp: db.Comp, techs: Seq[Tech]): Comp = Comp(
    id                = comp._id.stringify,
    name              = comp.name,
    website           = new URL(comp.website),
    location          = comp.location,
    employeeCount     = comp.employeeCount,
    codersCount       = comp.codersCount,
    femaleCodersCount = comp.femaleCodersCount,
    note              = comp.note,
    techs             = techs,
    joel              = comp.joel
  )
}