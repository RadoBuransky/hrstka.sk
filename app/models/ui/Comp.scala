package models.ui

import models.domain

case class Comp(id: String,
                name: String,
                website: String,
                city: String,
                employeeCount: Option[Int],
                codersCount: Option[Int],
                femaleCodersCount: Option[Int],
                note: String,
                products: Boolean,
                services: Boolean,
                internal: Boolean,
                techs: Seq[String],
                joel: Set[Int])

object Comp {
  def apply(comp: domain.Comp) = new Comp(
    id                = comp.id,
    name              = comp.name,
    website           = comp.website.toString,
    city              = comp.city.sk,
    employeeCount     = comp.employeeCount,
    codersCount       = comp.codersCount,
    femaleCodersCount = comp.femaleCodersCount,
    note              = comp.note,
    products          = comp.products,
    services          = comp.services,
    internal          = comp.internal,
    techs             = comp.techs.map(_.name),
    joel              = comp.joel)
}