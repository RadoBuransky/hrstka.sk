package sk.hrstka.models.domain

import java.net.URL

import sk.hrstka.models
import sk.hrstka.models.domain.Identifiable.Id

case class Comp(id: Id,
                name: String,
                website: URL,
                city: City,
                employeeCount: Option[Int],
                codersCount: Option[Int],
                femaleCodersCount: Option[Int],
                note: String,
                products: Boolean,
                services: Boolean,
                internal: Boolean,
                techRatings: Set[TechRating],
                joel: Set[Int]) extends Identifiable {
  def rank: Double = if (techRatings.isEmpty) 0.0 else techRatings.map(_.value).sum / techRatings.size.toDouble
}

object CompFactory {
  def apply(comp: models.db.Comp, techRatings: Set[TechRating], city: City): Comp = Comp(
    id                = comp._id.stringify,
    name              = comp.name,
    website           = new URL(comp.website),
    city              = city,
    employeeCount     = comp.employeeCount,
    codersCount       = comp.codersCount,
    femaleCodersCount = comp.femaleCodersCount,
    note              = comp.note,
    products          = comp.products,
    services          = comp.services,
    internal          = comp.internal,
    techRatings       = techRatings,
    joel              = comp.joel
  )
}