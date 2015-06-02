package models.api

case class Tech(id: String,
                handle: String,
                name: String,
                category: String,
                website: String,
                rating: Double)

object Tech {
  def fromDomain(techRating: models.domain.TechRating) = Tech(
    id        = techRating.tech.id,
    handle    = techRating.tech.handle.value,
    name      = techRating.tech.name,
    category  = techRating.tech.category.handle.value,
    website   = techRating.tech.website.toString,
    rating    = techRating.value
  )
}
