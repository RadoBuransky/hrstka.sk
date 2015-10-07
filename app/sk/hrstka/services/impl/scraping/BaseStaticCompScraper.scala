package sk.hrstka.services.impl.scraping

import java.net.URI

import org.apache.commons.lang3.StringUtils
import org.jsoup.Jsoup

import scala.annotation.tailrec

case class StaticCompScraperResult(name: String,
                                   website: URI,
                                   soTags: Set[String],
                                   employeeCount: Option[String])

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

    // Scrape posting main contents
    val soTags = findSoTags(doc.select(mainSelector).text())

    // Employee count
    val employeeCount = employeesPattern.findFirstMatchIn(doc.select(mainSelector).text()) match {
      case Some(numMatch) => Some(numMatch.subgroups(0))
      case _ => None
    }

    StaticCompScraperResult(name, website, soTags, employeeCount)
  }

  protected def nameSelector: String
  protected def websiteSelector: String
  protected def mainSelector: String

  private def findSoTags(text: String): Set[String] = {
    val foundTags = text.split("\\s+").flatMap { word =>
      val distances = StackoverflowTags.meaningful.map { tag =>
        tag -> StringUtils.getJaroWinklerDistance(tag, word)
      }

      val (bestTag, bestDistance) = distances.toList.sortBy(-1 * _._2).head
      if (bestDistance > 0.91)
        Some(bestTag)
      else
        None
    }

    foundTags.toSet
  }

  @tailrec
  private def stripSuffixes(s: String, suffixes: List[String]): String =
    suffixes.headOption match {
      case Some(suffix) => stripSuffixes(s.trim.stripSuffix(suffix), suffixes.tail)
      case None => s.trim
    }
}

private object BaseStaticCompScraper {
  val compNameStripSuffixes = List(", s.r.o.", ", spol. s r.o.", ", s. r. o.", ", a. s.", ", a.s.", "s.r.o.")
  val employeesPattern = """(\d+(-\d+)?)\s+(employees|zamestnancov)""".r
}