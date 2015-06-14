package sk.hrstka.models.ui

import sk.hrstka.models

case class Comp(id: String,
                name: String,
                website: String,
                city: City,
                employeeCount: Option[Int],
                codersCount: Option[Int],
                femaleCodersCount: Option[Int],
                note: String,
                products: Boolean,
                services: Boolean,
                internal: Boolean,
                techRatings: Seq[TechRating],
                joel: Map[Int, String]) {
  def maleCodersCount: Option[Int] = for {
    coders <- codersCount
    females <- femaleCodersCount
  } yield coders - females
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

  def apply(comp: models.domain.Comp) = new Comp(
    id                = comp.id.value,
    name              = comp.name,
    website           = comp.website.toString,
    city              = CityFactory(comp.city),
    employeeCount     = comp.employeeCount,
    codersCount       = comp.codersCount,
    femaleCodersCount = comp.femaleCodersCount,
    note              = comp.note,
    products          = comp.products,
    services          = comp.services,
    internal          = comp.internal,
    techRatings       = comp.techRatings.map(TechRatingFactory.apply),
    joel              = comp.joel.map(j => (j + 1) -> joelQuestions(j)).toMap)
}