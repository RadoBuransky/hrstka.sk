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
  import ProfesiaScrapingService._

  override def scrape: Future[ScrapingResult] = compRepository.all().map { dbComps =>
    val doc = Jsoup.connect(ProfesiaScrapingService.url.toString).get()

    // Scrape company names and make them distinct
    val compNames = scrapeCompNames(doc).distinct

    // Do we already have this company?
    val scrapedCompanies = compNames.map { compName =>
      val isNew = !dbComps.exists { dbComp =>
        StringUtils.getJaroWinklerDistance(dbComp.name.toLowerCase, compName.toLowerCase) > 0.8
      }

      ScrapedComp(compName, isNew)
    }

    ScrapingResult(scrapedCompanies.sortBy(!_.isNew))
  }

  @tailrec
  private def stripSuffixes(s: String, suffixes: List[String]): String =
    suffixes.headOption match {
      case Some(suffix) => stripSuffixes(s.trim.stripSuffix(suffix), suffixes.tail)
      case None => s.trim
    }

  private def scrapeCompNames(doc: Document): Seq[String] = {
    doc.select("#agentMatchedListWrapperForm li.list-row > div.row").toList.map(_.html()).flatMap {
      case divRowPattern(rawCompanyName) => Some(stripSuffixes(rawCompanyName, companyNameSuffixes))
      case other => {
        Logger.info(other)
        None
      }
    }
  }
}

private object ProfesiaScrapingService {
  val url = new URI("http://www.profesia.sk/offer_agent_details.php?no_limit=1&rulogin=55efbaaf0fad02142a562bc5ec5d3455")
  val divRowPattern = """(?s).*<br>(.*)<br>.*""".r
  val companyNameSuffixes = List(", s.r.o.", ", spol. s r.o.", ", s. r. o.", ", a. s.", ", a.s.", "s.r.o.")
}