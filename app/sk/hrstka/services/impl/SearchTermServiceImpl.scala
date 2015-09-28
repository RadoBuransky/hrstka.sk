package sk.hrstka.services.impl

import com.google.inject.{Inject, Singleton}
import sk.hrstka.models.domain.{City, CompSearch, TechRating}
import sk.hrstka.services.{LocationService, SearchTermService, TechService}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class SearchTermServiceImpl @Inject() (techService: TechService,
                                       locationService: LocationService) extends SearchTermService {
  import SearchTermServiceImpl._

  override def compSearch(query: String): Future[CompSearch] = {
    for {
      techs <- techService.allRatings()
      cities <- locationService.allCities()
    } yield compSearch(query, techs, cities)
  }

  private def compSearch(query: String, techs: Iterable[TechRating], cities: Traversable[City]): CompSearch = {
    ???
  }

  private[impl] def tokens(query: String): Seq[String] =
    termRegex.findAllMatchIn(query.toLowerCase).map(_.group(0)).toSeq
}

private object SearchTermServiceImpl {
  val termRegex = """([a-z0-9\+/]+)""".r
}
