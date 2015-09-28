package sk.hrstka.services

import com.google.inject.ImplementedBy
import sk.hrstka.models.domain.CompSearch
import sk.hrstka.services.impl.SearchTermServiceImpl

import scala.concurrent.Future

/**
 * Search term service.
 */
@ImplementedBy(classOf[SearchTermServiceImpl])
trait SearchTermService {
  /**
   * Process raw search query string and map it to domain model.
   *
   * @param query Raw search query.
   * @return Domain representation of the search query.
   */
  def compSearch(query: String): Future[CompSearch]
}
