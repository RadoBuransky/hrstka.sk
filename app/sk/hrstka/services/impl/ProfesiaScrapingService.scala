package sk.hrstka.services.impl

import java.net.URI
import javax.inject.{Inject, Singleton}

import org.apache.commons.lang3.StringUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.Logger
import sk.hrstka.models.domain.{ScrapedComp, ScrapingResult}
import sk.hrstka.repositories.CompRepository
import sk.hrstka.services.ScrapingService

import scala.annotation.tailrec
import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ProfesiaScrapingService @Inject() (compRepository: CompRepository) extends ScrapingService {
  import sk.hrstka.services.impl.ProfesiaScrapingService._

  override def scrape: Future[ScrapingResult] = compRepository.all().map { dbComps =>
    // Scrape company heads
    val compHeads = scrapeCompHeadsFromAllPages(1, Nil)

    // Do we already have this company?
    val scrapedCompanies = compHeads.map { compHead =>
      val isNew = !dbComps.exists { dbComp =>
        StringUtils.getJaroWinklerDistance(dbComp.name.toLowerCase, compHead.name.toLowerCase) > 0.8
      }

      ScrapedComp(compHead.name, isNew)
    }

    ScrapingResult(scrapedCompanies.sortBy(!_.isNew))
  }

  @tailrec
  private def scrapeCompHeadsFromAllPages(pageNum: Int, acc: List[ScrapedCompHead]): List[ScrapedCompHead] = {
    // Scape HTML page
    val doc = Jsoup.connect(ProfesiaScrapingService.url(pageNum).toString).get()

    // Scrape company names and make them distinct
    scrapeCompHeadsFromSingleDoc(doc) match {
      case Nil => acc
      case other => scrapeCompHeadsFromAllPages(pageNum + 1, acc ::: other)
    }
  }

  private def scrapeCompHeadsFromSingleDoc(doc: Document): List[ScrapedCompHead] = {
    doc.select("#agentMatchedListWrapperForm li.list-row > div.row").toList.map { divRow =>
      val uri = new URI(hostName + divRow.select("a.title").attr("href"))

      val name = divRow.html() match {
        case compNamePattern(rawCompanyName) => stripSuffixes(rawCompanyName, compNameStripSuffixes)
        case other => "Raw HTML: " + other
      }

      ScrapedCompHead(name, uri)
    }
  }

  @tailrec
  private def stripSuffixes(s: String, suffixes: List[String]): String =
    suffixes.headOption match {
      case Some(suffix) => stripSuffixes(s.trim.stripSuffix(suffix), suffixes.tail)
      case None => s.trim
    }

  private case class ScrapedCompHead(name: String, url: URI)
}

private object ProfesiaScrapingService {
  private val hostName = """www.profesia.sk"""
  def url(pageNum: Int = 1) = new URI(s"""http://www.profesia.sk/offer_agent_details.php?no_limit=1&rulogin=55efbaaf0fad02142a562bc5ec5d3455&page_num=$pageNum""")
  val compNamePattern = """(?s).*<br>(.*)<br>.*""".r
  val compNameStripSuffixes = List(", s.r.o.", ", spol. s r.o.", ", s. r. o.", ", a. s.", ", a.s.", "s.r.o.")
}