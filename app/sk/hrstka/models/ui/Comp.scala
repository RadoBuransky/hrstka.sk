package sk.hrstka.models.ui

import sk.hrstka.models

case class Comp(id: String,
                name: String,
                website: String,
                city: City,
                employeeCount: Option[Int],
                maleCodersCount: Option[Int],
                femaleCodersCount: Option[Int],
                note: String,
                products: Boolean,
                services: Boolean,
                internal: Boolean,
                techRatings: Seq[TechRating],
                joel: Set[Int])

object CompFactory {
  def apply(comp: models.domain.Comp) = new Comp(
    id                = comp.id.value,
    name              = comp.name,
    website           = comp.website.toString,
    city              = CityFactory(comp.city),
    employeeCount     = comp.employeeCount,
    maleCodersCount   = comp.codersCount.map(_ - comp.femaleCodersCount.getOrElse(0)),
    femaleCodersCount = comp.femaleCodersCount,
    note              = comp.note,
    products          = comp.products,
    services          = comp.services,
    internal          = comp.internal,
    techRatings       = comp.techRatings.map(TechRatingFactory.apply),
    joel              = comp.joel)
}