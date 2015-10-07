package sk.hrstka.models.ui

import java.net.URI

import sk.hrstka.models.domain

/**
 * Scraped company. Result of srcaping a HTML page.
 */
case class ScrapedComp(name: String,
                       isNew: Boolean,
                       tags: Set[String],
                       postingUrl: URI,
                       employeeCount: Option[String])

object ScrapedCompFactory {
  def apply(scrapedComp: domain.ScrapedComp): ScrapedComp = {
    ScrapedComp(
      name          = scrapedComp.name,
      isNew         = scrapedComp.isNew,
      tags          = scrapedComp.tags,
      postingUrl    = scrapedComp.postingUrl,
      employeeCount = scrapedComp.employeeCount
    )
  }
}
