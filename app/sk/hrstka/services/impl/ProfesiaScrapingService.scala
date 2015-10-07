package sk.hrstka.services.impl

import java.net.URI
import javax.inject.{Inject, Singleton}

import org.apache.commons.lang3.StringUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import sk.hrstka.models.domain.{BusinessNumber, ScrapedComp, ScrapedTag, ScrapingResult}
import sk.hrstka.repositories.{CompRepository, TechRepository}
import sk.hrstka.services.ScrapingService
import sk.hrstka.services.impl.scraping.ProfesiaStaticCompScraper

import scala.annotation.tailrec
import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ProfesiaScrapingService @Inject() (compRepository: CompRepository,
                                         techRepository: TechRepository) extends ScrapingService {
  import sk.hrstka.services.impl.ProfesiaScrapingService._

  override def scrape: Future[ScrapingResult] = compRepository.all().flatMap { dbComps =>
    techRepository.all().map { dbTechs =>
      // Scrape posting URLs
      val scrapedComps = scrapeAllPostingUrls(1, Nil).map { postingUrl =>
        // Scrape it
        val staticCompScraperResult = ProfesiaStaticCompScraper(postingUrl)

        // Do we already have this company?
        val existingComp = dbComps.find { dbComp =>
          StringUtils.getJaroWinklerDistance(dbComp.name.toLowerCase, staticCompScraperResult.name.toLowerCase) > 0.8
        }

        val scrapedTags = staticCompScraperResult.soTags.map { soTag =>
          // Is this SO tag new for this company<
          val newForComp = existingComp.isEmpty || existingComp.get.techs.contains(soTag)

          ScrapedTag(
            name        = soTag,
            newTech     = !dbTechs.exists(_.handle == soTag),
            newForComp  = newForComp)
        }

        ScrapedComp(
          name = staticCompScraperResult.name,
          businessNumber = existingComp.map(c => BusinessNumber(c.businessNumber)),
          tags = scrapedTags.toSeq,
          postingUrls = Seq(postingUrl),
          employeeCount = staticCompScraperResult.employeeCount
        )
      }

      // Group comps by name and merge
      val mergedComps = scrapedComps.groupBy(_.name).map { groupedComps =>
        ScrapedComp(
          name = groupedComps._1,
          businessNumber = groupedComps._2.head.businessNumber,
          tags = groupedComps._2.flatMap(_.tags).distinct.sortBy(st => (!st.newTech, !st.newForComp)),
          postingUrls = groupedComps._2.flatMap(_.postingUrls),
          employeeCount = groupedComps._2.flatMap(_.employeeCount).headOption
        )

      }

      ScrapingResult(mergedComps.toSeq.sortBy(_.businessNumber.isDefined))
    }
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