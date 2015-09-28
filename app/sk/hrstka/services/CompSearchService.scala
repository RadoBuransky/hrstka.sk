package sk.hrstka.services

import com.google.inject.ImplementedBy
import sk.hrstka.models.domain.{CompSearchRank, Comp, CompSearchQuery}
import sk.hrstka.services.impl.CompSearchServiceImpl

import scala.concurrent.Future

/**
 * Company search service.
 */
@ImplementedBy(classOf[CompSearchServiceImpl])
trait CompSearchService {
  /**
   * Process raw search query string and map it to domain model.
   *
   * @param query Raw search query.
   * @return Domain representation of the search query.
   */
  def compSearchQuery(query: String): Future[CompSearchQuery]

  /**
   * Computes rand for the given search query and a company.
   *
   * @param query Company search query.
   * @param comp Company to compute the rank for.
   * @return Search rank.
   */
  def rank(query: CompSearchQuery, comp: Comp): CompSearchRank
}
