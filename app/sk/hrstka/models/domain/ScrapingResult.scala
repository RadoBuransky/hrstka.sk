package sk.hrstka.models.domain

import java.net.URI

case class ScrapedComp(name: String,
                       isNew: Boolean,
                       tags: Set[String],
                       postingUrl: URI,
                       employeeCount: Option[String])

case class ScrapingResult(companies: Seq[ScrapedComp])
