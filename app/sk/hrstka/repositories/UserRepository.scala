package sk.hrstka.repositories

import com.google.inject.ImplementedBy
import sk.hrstka.models.db.Identifiable.Id
import sk.hrstka.models.db.User
import sk.hrstka.repositories.mongoDb.MongoUserRepository

import scala.concurrent.Future

/**
 * User repository.
 */
@ImplementedBy(classOf[MongoUserRepository])
trait UserRepository {
  /**
   * Inserts a new user.
   *
   * @param user User to insert.
   * @return Identifier of the inserted user.
   */
  def insert(user: User): Future[Id]

  /**
   * Finds an user by the email.
   *
   * @param email Email to find an user for.
   * @return Find result.
   */
  def findByEmail(email: String): Future[Option[User]]
}
