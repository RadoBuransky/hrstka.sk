package sk.hrstka.models.ui

import java.net.URL

import sk.hrstka

case class Tech(id: String,
                handle: String,
                categoryHandle: String,
                name: String,
                website: URL)

object TechFactory {
  def apply(tech: hrstka.models.domain.Tech) = new Tech(
    id              = tech.id,
    handle          = tech.handle.value,
    categoryHandle  = tech.category.handle.value,
    name            = tech.name,
    website         = tech.website)
}