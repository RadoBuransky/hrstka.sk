package sk.hrstka.services.impl

import com.google.inject.{Inject, Singleton}
import sk.hrstka.models.domain._
import sk.hrstka.services.{CompSearchService, LocationService, TechService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class CompSearchServiceImpl @Inject() (techService: TechService,
                                       locationService: LocationService) extends CompSearchService {
  import CompSearchServiceImpl._

  override def compSearchQuery(query: String): Future[CompSearchQuery] = {
    for {
      techs <- techService.allRatings()
      techHandles = techs.map(_.tech.handle)
      cities <- locationService.allCities()
      cityHandles = cities.map(_.handle)
    } yield compSearch(query, techHandles, cityHandles)
  }

  override def rank(query: CompSearchQuery, comp: Comp): CompSearchRank = {
    if (query.terms.isEmpty)
      // No search, trivial match
      MatchedRank(1.0)
    else {
      // Count individual term types
      val cityTerms = query.cityTerms
      val techTerms = query.techTerms
      val fulltextTerms = query.fulltextTerms

      // Count matches
      val matchedCities = cityTerms.map(_.cityHandle).count(comp.cities.map(_.handle))
      val matchedTechs = techTerms.map(_.techHandle).count(comp.techRatings.map(_.tech.handle).toSet)

      if (fulltextTerms.isEmpty) {
        if ((matchedCities != cityTerms.size) ||
            (matchedTechs != techTerms.size))
          NoMatchRank
        else
          MatchedRank(1.0)
      }
      else
        fulltextMatch(fulltextTerms, comp, (matchedCities + matchedTechs)*10000)
    }
  }

  private[impl] def fulltextMatch(fulltextTerms: Set[FulltextSearchTerm], comp: Comp, initialRank: Int): CompSearchRank = {
    // Match company city name
    val compCityNames = comp.cities.map(_.name.toLowerCase)
    val citiesMatch = fulltextTerms.count(term => compCityNames.exists(_.indexOf(term.text) >= 0))

    // Match company tech name
    val compTechNames = comp.techRatings.map(_.tech.name.toLowerCase)
    val techsMatch = fulltextTerms.count(term => compTechNames.exists(_.indexOf(term.text) >= 0))

    // Match company name
    val compName = comp.name.toLowerCase
    val nameMatch = fulltextTerms.count(term => compName.indexOf(term.text) >= 0)

    // Match company URL
    val compWebsite = comp.website.toString.toLowerCase
    val websiteMatch = fulltextTerms.count(term => compWebsite.indexOf(term.text) >= 0)

    // Match company note
    val compMarkdownNote = comp.markdownNote.toLowerCase
    val noteMatch = fulltextTerms.count(term => compMarkdownNote.indexOf(term.text) >= 0)

    // Match company business number
    val compBusinessNumber = comp.businessNumber.value.toLowerCase
    val businessNumberMatch = fulltextTerms.count(term => compBusinessNumber.indexOf(term.text) >= 0)

    if (citiesMatch + techsMatch + nameMatch + websiteMatch + noteMatch + businessNumberMatch == 0)
      NoMatchRank
    else
      MatchedRank(initialRank + nameMatch*1000 + websiteMatch*100 + noteMatch + businessNumberMatch + citiesMatch + techsMatch)
  }

  private def compSearch(query: String, techHandles: Iterable[Handle], cityHandles: Traversable[Handle]): CompSearchQuery = {
    def tokenToTerm(token: String): CompSearchTerm = {
      // Is it a tech?
      techHandles.find(_.value == token) match {
        case Some(techHandle) => TechSearchTerm(techHandle)
        case None =>
          // Is it a city?
          cityHandles.find(_.value == token) match {
            case Some(cityHandle) => CitySearchTerm(cityHandle)

            // Do full text
            case None => FulltextSearchTerm(token)
          }
      }
    }

    CompSearchQuery(queryToTokens(query).map(tokenToTerm))
  }

  private[impl] def queryToTokens(query: String): Set[String] =
    termRegex.findAllMatchIn(HandleFactory.removeDiacritics(query).toLowerCase).map(_.group(0)).toSet
}

private object CompSearchServiceImpl {
  val termRegex = """([a-z0-9\+/#\.]+)""".r
}
