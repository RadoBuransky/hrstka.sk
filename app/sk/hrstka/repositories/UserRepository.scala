package sk.hrstka.repositories

import com.google.inject.ImplementedBy
import sk.hrstka.models.db.Identifiable.Id
import sk.hrstka.models.db.{Identifiable, User}
import sk.hrstka.repositories.mongoDb.MongoUserRepository

import scala.concurrent.Future

@ImplementedBy(classOf[MongoUserRepository])
trait UserRepository {
  def insert(user: User): Future[Id]
  def findByEmail(email: String): Future[Option[User]]
}
