package sk.hrstka.repositories

import com.google.inject.ImplementedBy
import sk.hrstka.models.db.Identifiable.Id
import sk.hrstka.models.db.TechVote
import sk.hrstka.repositories.mongoDb.MongoTechVoteRepository

import scala.concurrent.Future

/**
 * Technology vote repository.
 */
@ImplementedBy(classOf[MongoTechVoteRepository])
trait TechVoteRepository {
  /**
   * Persists a vote of the user for the technology.
   *
   * @param techId Technology identifier.
   * @param userId User identifier.
   * @param value Vote value.
   * @return true if vote has been changed, false if the value is the same as the persisted one.
   */
  def vote(techId: Id, userId: Id, value: Int): Future[Boolean]

  /**
   * Finds a vote value for the user and the technology.
   *
   * @param techId Technology identifier.
   * @param userId User identifier.
   * @return Vote value if exists, None otherwise.
   */
  def findValue(techId: Id, userId: Id): Future[Option[Int]]

  /**
   * Gets all votes for the user unordered.
   *
   * @param userId User identifier.
   * @return Unordered collection of user's votes.
   */
  def all(userId: Option[Id]): Future[Traversable[TechVote]]
}