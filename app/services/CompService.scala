package services

import com.google.inject.ImplementedBy
import models.domain.Identifiable._
import models.domain._
import services.impl.CompServiceImpl

import scala.concurrent.Future

/**
 * Company service.
 */
@ImplementedBy(classOf[CompServiceImpl])
trait CompService {
  /**
   * Insert or update a company.
   *
   * @param comp Company to insert or update.
   * @param techHandles Handles of technologies that the company uses.
   * @param userId The current user.
   * @return Identifier of the company.
   */
  def upsert(comp: Comp, techHandles: Set[Handle], userId: Id): Future[Identifiable.Id]

  /**
   * Gets the company if exists or fails otherwise.
   *
   * @param compId Company identifier.
   * @return Company for the identifier.
   */
  def get(compId: Id): Future[Comp]

  /**
   * Gets all companies for the provided city AND tech.
   *
   * @param city Filter companies for the city.
   * @param tech Filter companies for the technology.
   * @return Found companies.
   */
  def all(city: Option[Handle] = None, tech: Option[Handle] = None): Future[Seq[Comp]]

  /**
   * Ordered list of top few companies with the most number of female programmers.
   *
   * @return Ordered list of companies.
   */
  def topWomen(): Future[Seq[Comp]]
}
