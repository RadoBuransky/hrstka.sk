package sk.hrstka.models.api

import sk.hrstka.models.domain
import sk.hrstka.models.domain.CompRatingFactory

case class Comp(id: String,
                name: String,
                website: String,
                cityHandle: String,
                employeeCount: Option[Int],
                codersCount: Option[Int],
                femaleCodersCount: Option[Int],
                note: String,
                products: Boolean,
                services: Boolean,
                internal: Boolean,
                techHandles: Seq[String],
                joel: Set[Int],
                hrstkaUrl: String,
                rating: BigDecimal)

object CompFactory {
  def fromDomain(comp: domain.Comp, url: String) = Comp(
    id                = comp.id.value,
    name              = comp.name,
    website           = comp.website.toString,
    cityHandle        = comp.city.handle.value,
    employeeCount     = comp.employeeCount,
    codersCount       = comp.codersCount,
    femaleCodersCount = comp.femaleCodersCount,
    note              = comp.note,
    products          = comp.products,
    services          = comp.services,
    internal          = comp.internal,
    techHandles       = comp.techRatings.map(_.tech.handle.value),
    joel              = comp.joel,
    hrstkaUrl         = url.toString,
    rating            = CompRatingFactory(comp).value
  )
}
