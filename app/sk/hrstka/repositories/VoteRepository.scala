package sk.hrstka.repositories

import com.google.inject.ImplementedBy
import sk.hrstka.models.db.Identifiable.Id
import sk.hrstka.models.db.{Vote, CompVote, TechVote}
import sk.hrstka.repositories.mongoDb.{MongoCompVoteRepository, MongoTechVoteRepository}

import scala.concurrent.Future

/**
 * Common interface for vote repositories.
 * @tparam TEntity Entity type.
 */
trait VoteRepository[+TEntity <: Vote] {
  /**
   * Persists a vote of the user for the entity.
   *
   * @param entityId Entity identifier.
   * @param userId User identifier.
   * @param value Vote value.
   * @return true if vote has been changed, false if the value is the same as the persisted one.
   */
  def vote(entityId: Id, userId: Id, value: Int): Future[Boolean]

  /**
   * Finds a vote value for the user and the entity.
   *
   * @param entityId Entity identifier.
   * @param userId User identifier.
   * @return Vote value if exists, None otherwise.
   */
  def findValue(entityId: Id, userId: Id): Future[Option[Int]]

  /**
   * Gets all votes unordered.
   *
   * @param userId User identifier to filter votes for. If not provided, all votes are returnes.
   * @return Unordered collection of user's votes.
   */
  def all(userId: Option[Id]): Future[Traversable[TEntity]]
}

/**
 * Technology vote repository.
 */
@ImplementedBy(classOf[MongoTechVoteRepository])
trait TechVoteRepository extends VoteRepository[TechVote]

/**
 * Company vote repository.
 */
@ImplementedBy(classOf[MongoCompVoteRepository])
trait CompVoteRepository extends VoteRepository[CompVote]