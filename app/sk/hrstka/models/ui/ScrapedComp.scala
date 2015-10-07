package sk.hrstka.models.ui

import sk.hrstka.models.domain

/**
 * Scraped company. Result of srcaping a HTML page.
 */
case class ScrapedComp(name: String,
                       isNew: Boolean)

object ScrapedCompFactory {
  def apply(scrapedComp: domain.ScrapedComp): ScrapedComp = {
    ScrapedComp(
      name  = scrapedComp.name,
      isNew = scrapedComp.isNew
    )
  }
}
