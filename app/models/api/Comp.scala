package models.api

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
                techs: Set[String],
                joel: Set[Int])

object CompFactory {
  def fromDomain(comp: models.domain.Comp) = Comp(
    id                = comp.id.toString,
    name              = comp.name,
    website           = comp.website.toString,
    city              = comp.city.handle.value,
    employeeCount     = comp.employeeCount,
    codersCount       = comp.codersCount,
    femaleCodersCount = comp.femaleCodersCount,
    note              = comp.note,
    products          = comp.products,
    services          = comp.services,
    internal          = comp.internal,
    techs             = comp.techRatings.map(_.tech.handle.value),
    joel              = comp.joel
  )
}
