package repositories

import models.db.User
import reactivemongo.core.commands.LastError

import scala.concurrent.Future

trait UserRepository {
  def insert(user: User): Future[LastError]
  def findByEmail(email: String): Future[Option[User]]
}
