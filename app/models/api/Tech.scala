package models.api

case class Tech(id: String,
                name: String,
                rating: Double)

object Tech {
  def fromDomain(tech: models.domain.Tech) = Tech(
    id      = tech.id,
    name    = tech.name,
    rating  = tech.rating.value
  )
}
