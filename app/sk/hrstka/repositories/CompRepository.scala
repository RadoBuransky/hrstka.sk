package sk.hrstka.repositories

import com.google.inject.ImplementedBy
import sk.hrstka.models.db.Comp
import sk.hrstka.models.db.Identifiable._
import sk.hrstka.models.domain.BusinessNumber
import sk.hrstka.repositories.mongoDb.MongoCompRepository

import scala.concurrent.Future

/**
 * Repository for companies.
 */
@ImplementedBy(classOf[MongoCompRepository])
trait CompRepository {
  /**
   * Inserts or updates a company.
   *
   * @param comp Company to insert or update.
   * @return Identifier of the company.
   */
  def upsert(comp: Comp): Future[Id]

  /**
   * Gets a company for the business number if exists, fails otherwise.
   *
   * @param businessNumber Company business number.
   * @return Company.
   */
  def get(businessNumber: String): Future[Comp]

  /**
   * Gets all companies unordered.
   *
   * @return Found companies.
   */
  def all(): Future[Iterable[Comp]]
}
