package sk.hrstka.models.api

import sk.hrstka.models.domain.TechRating

case class Tech(handle: String,
                name: String,
                category: String,
                website: String,
                rating: BigDecimal,
                apiUrl: String)

object TechFactory {
  def fromDomain(techRating: TechRating, apiUrl: String) = Tech(
    handle    = techRating.tech.handle.value,
    name      = techRating.tech.name,
    category  = techRating.tech.category.handle.value,
    website   = techRating.tech.website.toString,
    rating    = techRating.value,
    apiUrl    = apiUrl
  )
}
