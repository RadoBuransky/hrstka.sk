package sk.hrstka.repositories

import com.google.inject.ImplementedBy
import sk.hrstka.models.db.Identifiable.Id
import sk.hrstka.models.db.{Identifiable, Tech}
import sk.hrstka.repositories.mongoDb.MongoTechRepository

import scala.concurrent.Future

/**
 * Technology repository.
 */
@ImplementedBy(classOf[MongoTechRepository])
trait TechRepository {
  /**
   * Inserts or updates a technology.
   *
   * @param tech Technology to insert or update.
   * @return Identifier of the technology.
   */
  def upsert(tech: Tech): Future[Id]

  /**
   * Removes a technology.
   *
   * @param handle Technology handle.
   * @return Handle of the removed technology.
   */
  def remove(handle: String): Future[String]

  /**
   * Gets technology for the handle if exists, fails otherwiswe.
   *
   * @param handle Handle to get the technology for.
   * @return Found technology.
   */
  def getByHandle(handle: String): Future[Tech]

  /**
   * Gets all technologies unordered.
   *
   * @return Found technologies.
   */
  def all(): Future[Traversable[Tech]]
}
