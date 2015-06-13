package sk.hrstka.models.ui

import sk.hrstka.models

case class Comp(id: String,
                name: String,
                website: String,
                city: City,
                employeeCount: Option[Int],
                codersCount: Option[Int],
                femaleCodersCount: Option[Int],
                note: String,
                products: Boolean,
                services: Boolean,
                internal: Boolean,
                techs: Seq[String],
                joel: Set[Int])

object CompFactory {
  def apply(comp: models.domain.Comp) = new Comp(
    id                = comp.id.value,
    name              = comp.name,
    website           = comp.website.toString,
    city              = CityFactory(comp.city),
    employeeCount     = comp.employeeCount,
    codersCount       = comp.codersCount,
    femaleCodersCount = comp.femaleCodersCount,
    note              = comp.note,
    products          = comp.products,
    services          = comp.services,
    internal          = comp.internal,
    techs             = comp.techRatings.map(_.tech.name),
    joel              = comp.joel)
}