package sk.hrstka.models.domain

case class ScrapedComp(name: String, isNew: Boolean)

case class ScrapingResult(companies: Seq[ScrapedComp])
