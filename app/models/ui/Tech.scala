package models.ui

import java.net.URL

import models._

case class Tech(id: String,
                handle: String,
                categoryHandle: String,
                name: String,
                website: URL)

object Tech {
  def apply(tech: domain.Tech) = new Tech(
    id              = tech.id,
    handle          = tech.handle.value,
    categoryHandle  = tech.category.handle.value,
    name            = tech.name,
    website         = tech.website)
}