package sk.hrstka.services

import com.google.inject.ImplementedBy
import sk.hrstka.models.domain.{City, Handle}
import sk.hrstka.services.impl.LocationServiceImpl

import scala.concurrent.Future

/**
 * Geographical location service. Currently cities only.
 */
@ImplementedBy(classOf[LocationServiceImpl])
trait LocationService {
  /**
   * Gets all cities ordered by number of companies.
   * 
   * @return All cities ordered.
   */
  def all(): Future[Seq[City]]

  /**
   * Gets city for the handle if exists, fails otherwise.
   * 
   * @param handle Handle to get the city for.
   * @return The city.
   */
  def get(handle: Handle): Future[City]

  /**
   * Get city for the human name if exists or creates a new one.
   * 
   * @param sk Slovak name of the city.
   * @return Existing or newly created city.
   */
  def getOrCreateCity(sk: String): Future[City]
}
