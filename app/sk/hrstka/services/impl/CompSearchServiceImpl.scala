package sk.hrstka.services.impl

import com.google.inject.{Inject, Singleton}
import play.api.Logger
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
      val cityTerms = query.terms.collect { case cityTerm: CitySearchTerm => cityTerm }
      val techTerms = query.terms.collect { case techTerm: TechSearchTerm => techTerm }
      val fulltextTerms = query.terms.collect { case fulltextTerm: FulltextSearchTerm => fulltextTerm }

      // Maximal possible rank
      val maxPossibleRank = (cityTerms.size + 1) * (techTerms.size + 1) + fulltextTerms.size

      // Count matches
      val matchedCities = cityTerms.map(_.cityHandle).count(comp.cities.map(_.handle))
      val matchedTechs = techTerms.map(_.techHandle).count(comp.techRatings.map(_.tech.handle).toSet)
      val matchedFulltext = 0

      if (matchedCities == 0 && matchedTechs == 0 && matchedFulltext == 0)
        NoMatchRank
      else
        MatchedRank(((matchedCities + 1) * (matchedTechs + 1) + matchedFulltext).toDouble / maxPossibleRank.toDouble)
    }
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
  val termRegex = """([a-z0-9\+/]+)""".r
}
