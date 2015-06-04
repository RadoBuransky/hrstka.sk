package sk.hrstka.repositories

import com.google.inject.ImplementedBy
import models.db.Identifiable.Id
import models.db.User
import reactivemongo.core.commands.LastError
import sk.hrstka.repositories.mongoDb.MongoUserRepository

import scala.concurrent.Future

@ImplementedBy(classOf[MongoUserRepository])
trait UserRepository {
  def insert(user: User): Future[Id]
  def findByEmail(email: String): Future[Option[User]]
}
