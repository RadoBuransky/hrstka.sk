package sk.hrstka.models.api

import sk.hrstka.models.domain

case class Comp(businessNumber: String,
                name: String,
                website: String,
                cityHandle: String,
                employeeCount: Option[Int],
                codersCount: Option[Int],
                womenCodersCount: Option[Int],
                markdown: String,
                products: Boolean,
                services: Boolean,
                internal: Boolean,
                techHandles: Seq[String],
                joel: Seq[Int],
                govBiz: Option[BigDecimal],
                htmlUrl: String,
                rating: BigDecimal)

object CompFactory {
  def fromDomain(compRating: domain.CompRating, htmlUrl: String) = Comp(
    businessNumber    = compRating.comp.businessNumber.value,
    name              = compRating.comp.name,
    website           = compRating.comp.website.toString,
    cityHandle        = compRating.comp.city.handle.value,
    employeeCount     = compRating.comp.employeeCount,
    codersCount       = compRating.comp.codersCount,
    womenCodersCount  = compRating.comp.femaleCodersCount,
    markdown          = compRating.comp.markdownNote,
    products          = compRating.comp.products,
    services          = compRating.comp.services,
    internal          = compRating.comp.internal,
    techHandles       = compRating.comp.techRatings.map(_.tech.handle.value),
    joel              = compRating.comp.joel.toSeq.sorted,
    govBiz            = compRating.comp.govBiz,
    htmlUrl           = htmlUrl,
    rating            = compRating.value
  )
}
