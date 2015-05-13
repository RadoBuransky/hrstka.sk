package models.api

case class Tech(id: String,
                handle: String,
                rating: Double)

object Tech {
  def fromDomain(tech: models.domain.Tech) = Tech(
    id      = tech.id,
    handle  = tech.handle.value,
    rating  = tech.rating.value
  )
}
