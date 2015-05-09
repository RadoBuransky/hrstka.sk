package models.api

case class Comp(id: String,
                name: String,
                website: String,
                location: String,
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
  def fromDomain(comp: models.domain.Comp) = Comp(
    id                = comp.id.toString,
    name              = comp.name,
    website           = comp.website.toString,
    location          = comp.city.handle,
    employeeCount     = comp.employeeCount,
    codersCount       = comp.codersCount,
    femaleCodersCount = comp.femaleCodersCount,
    note              = comp.note,
    products          = comp.products,
    services          = comp.services,
    internal          = comp.internal,
    techs             = comp.techs.map(_.name),
    joel              = comp.joel
  )
}
