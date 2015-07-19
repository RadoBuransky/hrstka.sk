package sk.hrstka.services

import com.google.inject.ImplementedBy
import sk.hrstka.models.domain.{Country, City, Handle}
import sk.hrstka.services.impl.cache.CachedLocationServiceImpl

import scala.concurrent.Future

/**
 * Geographical location service. Currently cities only.
 */
@ImplementedBy(classOf[CachedLocationServiceImpl])
trait LocationService {
  /**
   * Gets all countries ordered by number of companies.
   *
   * @return All countries ordered.
   */
  def countries(): Future[Seq[Country]]

  /**
   * Gets all cities ordered by number of companies.
   * 
   * @return All cities ordered.
   */
  def cities(): Future[Seq[City]]

  /**
   * Gets city for the handle if exists, fails otherwise.
   * 
   * @param handle Handle to get the city for.
   * @return The city.
   */
  def city(handle: Handle): Future[City]

  /**
   * Get city for the human name if exists or creates a new one.
   * 
   * @param sk Slovak name of the city.
   * @return Existing or newly created city.
   */
  def getOrCreateCity(sk: String): Future[City]
}
