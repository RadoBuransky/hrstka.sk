package sk.hrstka.models.api

import java.math.MathContext

import sk.hrstka.models.domain.TechRating

case class Tech(id: String,
                handle: String,
                name: String,
                category: String,
                website: String,
                rating: BigDecimal)

object TechFactory {
  def fromDomain(techRating: TechRating) = Tech(
    id        = techRating.tech.id.value,
    handle    = techRating.tech.handle.value,
    name      = techRating.tech.name,
    category  = techRating.tech.category.handle.value,
    website   = techRating.tech.website.toString,
    rating    = techRating.value
  )
}
