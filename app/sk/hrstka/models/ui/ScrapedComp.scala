package sk.hrstka.models.ui

import java.net.URI

import sk.hrstka.models.domain

case class ScrapedTag(name: String,
                      newTech: Boolean,
                      newForComp: Boolean)

/**
 * Scraped company. Result of srcaping a HTML page.
 */
case class ScrapedComp(name: String,
                       businessNumber: Option[String],
                       tags: Seq[ScrapedTag],
                       postingUrls: Seq[URI],
                       employeeCount: Option[String],
                       hrstkaUrl: URI)

object ScrapedCompFactory {
  def apply(scrapedComp: domain.ScrapedComp, hrstkaUrl: URI): ScrapedComp = {
    ScrapedComp(
      name            = scrapedComp.name,
      businessNumber  = scrapedComp.businessNumber.map(_.value),
      tags            = scrapedComp.tags.map(ScrapedTagFactory.apply),
      postingUrls     = scrapedComp.postingUrls,
      employeeCount   = scrapedComp.employeeCount,
      hrstkaUrl       = hrstkaUrl
    )
  }
}

object ScrapedTagFactory {
  def apply(scrapedTag: domain.ScrapedTag): ScrapedTag =
    ScrapedTag(
      name        = scrapedTag.name,
      newTech     = scrapedTag.newTech,
      newForComp  = scrapedTag.newForComp
    )
}