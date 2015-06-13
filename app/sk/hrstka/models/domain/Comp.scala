package sk.hrstka.models.domain

import java.net.URL

import sk.hrstka.models

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
                techRatings: Seq[TechRating],
                joel: Set[Int]) extends Identifiable {
  codersCount match {
    case Some(c) => employeeCount match {
      case Some(e) if e < c => throw new IllegalArgumentException(s"Number of all employees is lower than number of all coders!")
      case None => throw new IllegalArgumentException(s"Provide number of all employees!")
      case _ =>
    }
    case None =>
  }

  femaleCodersCount match {
    case Some(f) => codersCount match {
      case Some(c) if c < f => throw new IllegalArgumentException(s"Number of all coders is lower than number of female coders!")
      case None => throw new IllegalArgumentException(s"Provide number of all coders!")
      case _ =>
    }
    case None =>
  }
}

object CompFactory {
  def apply(comp: models.db.Comp, techRatings: Seq[TechRating], city: City): Comp = Comp(
    id                = Identifiable.fromBSON(comp._id),
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