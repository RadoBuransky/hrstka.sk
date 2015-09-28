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

  override def rank(query: CompSearchQuery, comp: Comp): CompSearchRank = ???

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
