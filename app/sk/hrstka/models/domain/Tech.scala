package sk.hrstka.models.domain

import java.net.URL

import sk.hrstka

/**
 * Technology
 *
 * @param id Unique identifier.
 * @param handle Human-friendly identifier.
 * @param category Platform, framework, database, ...
 * @param name Human name.
 * @param website URL to more info.
 */
case class Tech(id: Id,
                handle: Handle,
                category: TechCategory,
                name: String,
                website: URL) extends Identifiable

object TechFactory {
  def apply(src: hrstka.models.db.Tech): Tech =
    hrstka.models.domain.Tech(
      id        = Identifiable.fromBSON(src._id),
      handle    = Handle(src.handle),
      category  = TechCategory(src.categoryHandle),
      name      = src.name,
      website   = new URL(src.website))
}