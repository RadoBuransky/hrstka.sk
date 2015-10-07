package sk.hrstka.services.impl.scraping

import org.jsoup.Jsoup

import scala.annotation.tailrec

case class StaticCompScraperResult(name: String)

/**
 * Generic HTML company info scraper.
 */
abstract class BaseStaticCompScraper {
  import sk.hrstka.services.impl.scraping.BaseStaticCompScraper._

  def scrape(html: String): StaticCompScraperResult = {
    val doc = Jsoup.parse(html)
    val name = stripSuffixes(doc.select(compNameSelector).text(), compNameStripSuffixes)
    StaticCompScraperResult(name)
  }

  protected def compNameSelector: String

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