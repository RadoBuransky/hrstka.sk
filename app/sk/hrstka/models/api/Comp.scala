package sk.hrstka.models.api

import sk.hrstka.models.domain
import sk.hrstka.models.domain.CompRatingFactory

case class Comp(id: String,
                name: String,
                website: String,
                cityHandle: String,
                businessNumber: String,
                employeeCount: Option[Int],
                codersCount: Option[Int],
                femaleCodersCount: Option[Int],
                markdown: String,
                products: Boolean,
                services: Boolean,
                internal: Boolean,
                techHandles: Seq[String],
                joel: Seq[Int],
                govBiz: Option[BigDecimal],
                apiUrl: String,
                htmlUrl: String,
                rating: BigDecimal)

object CompFactory {
  def fromDomain(compRating: domain.CompRating, apiUrl: String, htmlUrl: String) = Comp(
    id                = compRating.comp.id.value,
    name              = compRating.comp.name,
    website           = compRating.comp.website.toString,
    cityHandle        = compRating.comp.city.handle.value,
    businessNumber    = compRating.comp.businessNumber.value,
    employeeCount     = compRating.comp.employeeCount,
    codersCount       = compRating.comp.codersCount,
    femaleCodersCount = compRating.comp.femaleCodersCount,
    markdown          = compRating.comp.note,
    products          = compRating.comp.products,
    services          = compRating.comp.services,
    internal          = compRating.comp.internal,
    techHandles       = compRating.comp.techRatings.map(_.tech.handle.value),
    joel              = compRating.comp.joel.toSeq.sorted,
    govBiz            = compRating.comp.govBiz,
    apiUrl            = apiUrl,
    htmlUrl           = htmlUrl,
    rating            = compRating.value
  )
}
