package sk.hrstka.models.domain

import java.net.URL

import sk.hrstka.models

case class Comp(id: Id,
                name: String,
                website: URL,
                city: City,
                businessNumber: String,
                employeeCount: Option[Int],
                codersCount: Option[Int],
                femaleCodersCount: Option[Int],
                note: String,
                products: Boolean,
                services: Boolean,
                internal: Boolean,
                techRatings: Seq[TechRating],
                joel: Set[Int],
                govBiz: Option[BigDecimal]) extends Identifiable {
  if (name.trim.isEmpty)
    throw new IllegalArgumentException(s"Name cannot be empty!")

  if (businessNumber.trim.isEmpty)
    throw new IllegalArgumentException(s"Business number cannot be empty!")

  employeeCount match {
    case Some(value) if value < 0 => throw new IllegalArgumentException(s"Number of employees cannot be negative! [$value]")
    case _ =>
  }

  codersCount match {
    case Some(value) if value < 0 => throw new IllegalArgumentException(s"Number of programmers cannot be negative! [$value]")
    case Some(c) => employeeCount match {
      case Some(e) if e < c => throw new IllegalArgumentException(s"Number of all employees is lower than number of all coders!")
      case None => throw new IllegalArgumentException(s"Provide number of all employees!")
      case _ =>
    }
    case _ =>
  }

  femaleCodersCount match {
    case Some(value) if value < 0 => throw new IllegalArgumentException(s"Number of female programmers cannot be negative! [$value]")
    case Some(f) => codersCount match {
      case Some(c) if c < f => throw new IllegalArgumentException(s"Number of all coders is lower than number of female coders!")
      case None => throw new IllegalArgumentException(s"Provide number of all coders!")
      case _ =>
    }
    case _ =>
  }

  govBiz match {
    case Some(value) if value < 0 || value > 100 => throw new IllegalArgumentException(s"Government business must be a number between 0 and 100! [$value]")
    case _ =>
  }

  if (joel.exists(_ > 11))
    throw new IllegalArgumentException(s"The Joel Test has 12 questions, not more!")
}

object CompFactory {
  def apply(comp: models.db.Comp, techRatings: Seq[TechRating], city: City): Comp = Comp(
    id                = Identifiable.fromBSON(comp._id),
    name              = comp.name,
    website           = new URL(comp.website),
    city              = city,
    businessNumber    = comp.businessNumber,
    employeeCount     = comp.employeeCount,
    codersCount       = comp.codersCount,
    femaleCodersCount = comp.femaleCodersCount,
    note              = comp.note,
    products          = comp.products,
    services          = comp.services,
    internal          = comp.internal,
    techRatings       = techRatings,
    joel              = comp.joel,
    govBiz            = comp.govBiz
  )
}