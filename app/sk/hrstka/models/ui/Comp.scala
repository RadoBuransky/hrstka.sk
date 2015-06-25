package sk.hrstka.models.ui

import java.text.DecimalFormat

import sk.hrstka.models

case class Comp(id: String,
                name: String,
                website: String,
                city: City,
                employeeCount: Option[Int],
                codersCount: Option[Int],
                femaleCodersCount: Option[Int],
                govRevenue: Option[BigDecimal],
                note: String,
                products: Boolean,
                services: Boolean,
                internal: Boolean,
                techRatings: Seq[TechRating],
                joel: Map[Int, String],
                rating: BigDecimal) {
  def maleCodersCount: Option[Int] = for {
    coders <- codersCount
    females <- femaleCodersCount
  } yield coders - females
}

object Comp {
  val format = new DecimalFormat("#.#")
}

object CompFactory {
  val joelQuestions = List(
    "Do you use source control?",
    "Can you make a build in one step?",
    "Do you make daily builds?",
    "Do you have a bug database?",
    "Do you fix bugs before writing new code?",
    "Do you have an up-to-date schedule?",
    "Do you have a spec?",
    "Do programmers have quiet working conditions?",
    "Do you use the best tools money can buy?",
    "Do you have testers?",
    "Do new candidates write code during their interview?",
    "Do you do hallway usability testing?"
  )

  /**
   * Converts company from domain model to UI.
   *
   * @param compRating Company rating in domain model.
   * @param formattedNote Properly formatted note. It can be HTML or Markdown depending on usage.
   * @return
   */
  def apply(compRating: models.domain.CompRating, formattedNote: String) = new Comp(
    id                = compRating.comp.id.value,
    name              = compRating.comp.name,
    website           = compRating.comp.website.toString,
    city              = CityFactory(compRating.comp.city),
    employeeCount     = compRating.comp.employeeCount,
    codersCount       = compRating.comp.codersCount,
    femaleCodersCount = compRating.comp.femaleCodersCount,
    govRevenue        = compRating.comp.govRevenue,
    note              = formattedNote,
    products          = compRating.comp.products,
    services          = compRating.comp.services,
    internal          = compRating.comp.internal,
    techRatings       = compRating.comp.techRatings.map(TechRatingFactory.apply),
    joel              = compRating.comp.joel.map(j => (j + 1) -> joelQuestions(j)).toMap,
    rating            = compRating.value)
}