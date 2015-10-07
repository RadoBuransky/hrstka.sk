package sk.hrstka.models.domain

import java.net.URI

case class ScrapedTag(name: String,
                      newTech: Boolean,
                      newForComp: Boolean)

case class ScrapedComp(name: String,
                       businessNumber: Option[BusinessNumber],
                       tags: Seq[ScrapedTag],
                       postingUrls: Seq[URI],
                       employeeCount: Option[String])

case class ScrapingResult(companies: Seq[ScrapedComp])
