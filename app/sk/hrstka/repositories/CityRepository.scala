package sk.hrstka.repositories

import com.google.inject.ImplementedBy
import sk.hrstka.models.db.City
import sk.hrstka.models.db.Identifiable.Id
import sk.hrstka.repositories.mongoDb.MongoCityRepository

import scala.concurrent.Future

/**
 * City repository.
 */
@ImplementedBy(classOf[MongoCityRepository])
trait CityRepository {
  /**
   * Inserts a new city.
   *
   * @param city City to insert.
   * @return Identifier of inserted city.
   */
  def insert(city: City): Future[Id]

  /**
   * Gets city by handle if exists, fails otherwise.
   *
   * @param handle Handle for the city to get.
   * @return Found city.
   */
  def getByHandle(handle: String): Future[City]

  /**
   * Finds city by handle.
   *
   * @param handle Handle to find the city for.
   * @return Find result.
   */
  def findByHandle(handle: String): Future[Option[City]]

  /**
   * Gets all cities unordered.
   *
   * @return All cities.
   */
  def all(): Future[Traversable[City]]
}
