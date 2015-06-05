package sk.hrstka.services

import com.google.inject.ImplementedBy
import sk.hrstka.models.domain.Identifiable.Id
import sk.hrstka.models.domain._
import sk.hrstka.services.impl.TechServiceImpl

import scala.concurrent.Future

/**
 * Technology service.
 */
@ImplementedBy(classOf[TechServiceImpl])
trait TechService {
  /**
   * Inserts or updates a technology.
   *
   * @param tech Technology to insert or update.
   * @return Identifier of the technology.
   */
  def upsert(tech: Tech): Future[Id]

  /**
   * Gets the technology if exists or fails otherwise.
   *
   * @param handle Technology handle.
   * @return Technology for the handle.
   */
  def getByHandle(handle: Handle): Future[Tech]

  /**
   * Gets all technologies with ratings ordered by rating value.
   *
   * @return All technologies with ratings.
   */
  def allRatings(): Future[Seq[TechRating]]

  /**
   * Gets all technology votes for the user unordered.
   *
   * @param userId User identifier.
   * @return All technology votes for the user.
   */
  def votesFor(userId: Id):Future[Traversable[TechVote]]

  /**
   * Increases value of user's vote for the technology.
   *
   * @param techId Technology identifier.
   * @param userId User identifier.
   * @return Nothing.
   */
  def voteUp(techId: Id, userId: Id): Future[Unit]

  /**
   * Decreases value of user's vote for the technology.
   * 
   * @param techId Technology identifier.
   * @param userId User identifier.
   * @return Nothing.
   */
  def voteDown(techId: Id, userId: Id): Future[Unit]

  /**
   * Gets all technology categories unordered.
   *
   * @return
   */
  def allCategories(): Future[Traversable[TechCategory]]
}
