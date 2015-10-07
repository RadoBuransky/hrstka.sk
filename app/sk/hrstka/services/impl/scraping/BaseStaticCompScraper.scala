package sk.hrstka.services.impl.scraping

import java.net.URI

import org.jsoup.Jsoup
import play.api.Logger

import scala.annotation.tailrec

case class StaticCompScraperResult(name: String, website: URI)

/**
 * Generic HTML company info scraper.
 */
abstract class BaseStaticCompScraper {
  import sk.hrstka.services.impl.scraping.BaseStaticCompScraper._

  def scrape(html: String): StaticCompScraperResult = {
    // Parse HTML
    val doc = Jsoup.parse(html)

    // Scrape company name
    val name = stripSuffixes(doc.select(nameSelector).text(), compNameStripSuffixes)

    // Scrape company website
    val website = new URI(doc.select(websiteSelector).attr("href"))

    StaticCompScraperResult(name, website)
  }

  protected def nameSelector: String
  protected def websiteSelector: String

  @tailrec
  private def stripSuffixes(s: String, suffixes: List[String]): String =
    suffixes.headOption match {
      case Some(suffix) => stripSuffixes(s.trim.stripSuffix(suffix), suffixes.tail)
      case None => s.trim
    }
}

private object BaseStaticCompScraper {
  val compNameStripSuffixes = List(", s.r.o.", ", spol. s r.o.", ", s. r. o.", ", a. s.", ", a.s.", "s.r.o.")
}