package sk.hrstka.models.domain

import java.net.URI

import sk.hrstka.models

case class Comp(id: Id,
                name: String,
                website: URI,
                cities: Set[City],
                businessNumber: BusinessNumber,
                employeeCount: Option[Int],
                codersCount: Option[Int],
                femaleCodersCount: Option[Int],
                markdownNote: String,
                products: Boolean,
                services: Boolean,
                internal: Boolean,
                techRatings: Seq[TechRating],
                joel: Set[Int],
                govBiz: Option[BigDecimal]) extends Identifiable {
  if (name.trim.isEmpty)
    throw new IllegalArgumentException(s"Name cannot be empty!")

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
    case Some(value) if value < 0 => throw new IllegalArgumentException(s"Number of women programmers cannot be negative! [$value]")
    case Some(f) => codersCount match {
      case Some(c) if c < f => throw new IllegalArgumentException(s"Number of all coders is lower than number of women coders!")
      case None => throw new IllegalArgumentException(s"Provide number of all coders!")
      case _ =>
    }
    case _ =>
  }

  govBiz match {
    case Some(value) if value < 0 || value > 100 => throw new IllegalArgumentException(s"Government business must be a number between 0 and 100! [$value]")
    case _ =>
  }

  if (joel.exists(_ < 0))
    throw new IllegalArgumentException(s"Illegal The Joel Test question!")

  if (joel.exists(_ > 11))
    throw new IllegalArgumentException(s"The Joel Test has 12 questions, not more!")
}

object CompFactory {
  def apply(comp: models.db.Comp, techRatings: Seq[TechRating], cities: Set[City]): Comp = Comp(
    id                = Identifiable.fromBSON(comp._id),
    name              = comp.name,
    website           = new URI(comp.website),
    cities            = cities,
    businessNumber    = BusinessNumber(comp.businessNumber),
    employeeCount     = comp.employeeCount,
    codersCount       = comp.codersCount,
    femaleCodersCount = comp.femaleCodersCount,
    markdownNote      = comp.note,
    products          = comp.products,
    services          = comp.services,
    internal          = comp.internal,
    techRatings       = techRatings,
    joel              = comp.joel,
    govBiz            = comp.govBiz
  )
}