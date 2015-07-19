package sk.hrstka.models.ui

import java.net.URI

import sk.hrstka

case class Tech(id: String,
                handle: String,
                categoryHandle: String,
                name: String,
                website: URI)

object TechFactory {
  def apply(tech: hrstka.models.domain.Tech) = new Tech(
    id              = tech.id.value,
    handle          = tech.handle.value,
    categoryHandle  = tech.category.handle.value,
    name            = tech.name,
    website         = tech.website)
}