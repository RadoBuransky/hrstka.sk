package sk.hrstka.models.ui

import java.text.DecimalFormat

import sk.hrstka.models

/**
 * Company user interface model.
 *
 * @param id Company identifier.
 * @param name Name.
 * @param website Website URL.
 * @param cities Cities that this company does business in.
 * @param businessNumber Business number (natural unique identifier)
 * @param employeeCount Total number of employees.
 * @param codersCount Number of programmers.
 * @param femaleCodersCount Number of female programmers.
 * @param note HTML or Markdown depending on usage.
 * @param products Does the company sell products?
 * @param services Does the company provide services?
 * @param internal Does the company have an internal programming department?
 * @param techRatings Technology ratings.
 * @param joel The Joel Test.
 * @param govBiz Government business percentage.
 */
case class Comp(id: String,
                name: String,
                website: String,
                cities: Seq[City],
                businessNumber: String,
                employeeCount: Option[Int],
                codersCount: Option[Int],
                femaleCodersCount: Option[Int],
                note: FormattedText,
                products: Boolean,
                services: Boolean,
                internal: Boolean,
                techRatings: Seq[TechRating],
                joel: Seq[(Int, String)],
                govBiz: Option[BigDecimal]) {
  def maleCodersCount: Option[Int] = for {
    coders <- codersCount
    females <- femaleCodersCount
  } yield coders - females

  def title = name
  def description = name + " in " + cities.map(_.en).mkString(", ") + " uses " + techRatings.map(_.tech.name).mkString(", ") + "."
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
   * @param comp Company n domain model.
   * @param formattedNote Properly formatted note. It can be HTML or Markdown depending on usage.
   * @return
   */
  def apply(comp: models.domain.Comp, formattedNote: FormattedText) = new Comp(
    id                = comp.id.value,
    name              = comp.name,
    website           = comp.website.toString,
    cities            = comp.cities.map(CityFactory.apply).toSeq,
    businessNumber    = comp.businessNumber.value,
    employeeCount     = comp.employeeCount,
    codersCount       = comp.codersCount,
    femaleCodersCount = comp.femaleCodersCount,
    note              = formattedNote,
    products          = comp.products,
    services          = comp.services,
    internal          = comp.internal,
    techRatings       = comp.techRatings.map(TechRatingFactory.apply),
    joel              = comp.joel.map(j => (j + 1) -> joelQuestions(j)).toSeq.sortBy(_._1),
    govBiz            = comp.govBiz)
}