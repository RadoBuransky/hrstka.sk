package sk.hrstka.services

import com.google.inject.ImplementedBy
import sk.hrstka.models.domain._
import sk.hrstka.services.impl.cache.CachedCompServiceImpl

import scala.concurrent.Future

/**
 * Company service.
 */
@ImplementedBy(classOf[CachedCompServiceImpl])
trait CompService {
  /**
   * Inserts or updates a company.
   *
   * @param comp Company to insert or update.
   * @param techHandles Handles of technologies that the company uses.
   * @param userId The current user.
   * @return Identifier of the company.
   */
  def upsert(comp: Comp, techHandles: Set[Handle], userId: Id): Future[BusinessNumber]

  /**
   * Gets the company if exists or fails otherwise.
   *
   * @param businessNumber Company business number.
   * @return Company for the business number.
   */
  def get(businessNumber: BusinessNumber): Future[Comp]

  /**
   * Gets all company ratings for the provided city AND tech ordered by their rating.
   *
   * @param city Filter companies for the city.
   * @param tech Filter companies for the technology.
   * @return Found companies.
   */
  def all(city: Option[Handle] = None, tech: Option[Handle] = None): Future[Seq[CompRating]]

  /**
   * Search for companies.
   *
   * @param query Raw company search query.
   * @return Found companies.
   */
  def search(query: String): Future[Seq[CompRating]]

  /**
   * Ordered list of top few companies with the most number of female programmers.
   *
   * @return Ordered list of companies.
   */
  def topWomen(): Future[Seq[CompRating]]

  /**
   * Gets vote for the user and the company.
   *
   * @param businessNumber Company business number.
   * @param userId User identifier.
   * @return Vote if exists.
   */
  def voteFor(businessNumber: BusinessNumber, userId: Id):Future[Option[CompVote]]

  /**
   * Increases value of user's vote for the company.
   *
   * @param businessNumber Company business number.
   * @param userId User identifier.
   * @return Nothing.
   */
  def voteUp(businessNumber: BusinessNumber, userId: Id): Future[Unit]

  /**
   * Decreases value of user's vote for the company.
   *
   * @param businessNumber Company business number.
   * @param userId User identifier.
   * @return Nothing.
   */
  def voteDown(businessNumber: BusinessNumber, userId: Id): Future[Unit]
}
