package repositories.mongoDb

import models.db.User
import play.api.libs.json.Json
import reactivemongo.core.commands.LastError
import repositories.UserRepository

import scala.concurrent.Future

class MongoUserRepository extends BaseMongoRepository(UserCollection) with UserRepository {
  override def insert(user: User): Future[LastError] = super.insert[User](user)
  override def findByEmail(email: String): Future[Option[User]] = find[User](Json.obj("email" -> email)).map(_.headOption)
}
