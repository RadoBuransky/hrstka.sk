package sk.hrstka.models.domain

case class ScrapedCompany(name: String, isNew: Boolean)

case class ScrapingResult(companies: Seq[ScrapedCompany])
