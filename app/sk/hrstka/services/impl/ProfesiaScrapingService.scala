package sk.hrstka.services.impl

import java.net.URI
import javax.inject.{Inject, Singleton}

import org.apache.commons.lang3.StringUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import sk.hrstka.models.domain.{ScrapedComp, ScrapingResult}
import sk.hrstka.repositories.CompRepository
import sk.hrstka.services.ScrapingService
import sk.hrstka.services.impl.scraping.ProfesiaStaticCompScraper

import scala.annotation.tailrec
import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ProfesiaScrapingService @Inject() (compRepository: CompRepository) extends ScrapingService {
  import sk.hrstka.services.impl.ProfesiaScrapingService._

  override def scrape: Future[ScrapingResult] = compRepository.all().map { dbComps =>
    // Scrape posting URLs
    val scrapedComps = scrapeAllPostingUrls(1, Nil).map { postingUrl =>
      val staticCompScraperResult = ProfesiaStaticCompScraper(postingUrl)

      val isNew = !dbComps.exists { dbComp =>
        StringUtils.getJaroWinklerDistance(dbComp.name.toLowerCase, staticCompScraperResult.name.toLowerCase) > 0.8
      }

      ScrapedComp(
        name          = staticCompScraperResult.name,
        isNew         = isNew,
        tags          = staticCompScraperResult.soTags,
        postingUrl    = postingUrl,
        employeeCount = staticCompScraperResult.employeeCount
      )
    }

    ScrapingResult(scrapedComps.sortBy(!_.isNew))
  }

  @tailrec
  private def scrapeAllPostingUrls(pageNum: Int, acc: List[URI]): List[URI] = {
    // Scape HTML page
    val doc = Jsoup.connect(ProfesiaScrapingService.url(pageNum).toString).get()

    // Scrape company names and make them distinct
    scrapePostingUrlsFromSingleDoc(doc) match {
      case Nil => acc
      case other => scrapeAllPostingUrls(pageNum + 1, acc ::: other)
    }
  }

  private def scrapePostingUrlsFromSingleDoc(doc: Document): List[URI] = {
    doc.select("#agentMatchedListWrapperForm li.list-row > div.row").toList.map { divRow =>
      new URI(hostName + divRow.select("a.title").attr("href"))
    }
  }
}

private object ProfesiaScrapingService {
  private val hostName = """http://www.profesia.sk"""
  def url(pageNum: Int = 1) = new URI(s"""http://www.profesia.sk/offer_agent_details.php?no_limit=1&rulogin=55efbaaf0fad02142a562bc5ec5d3455&page_num=$pageNum""")
}