package sk.hrstka.repositories.mongoDb

import com.google.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import sk.hrstka.models.db.JsonFormats._
import sk.hrstka.models.db.{JsonFormats, User}
import sk.hrstka.repositories.UserRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
final class MongoUserRepository @Inject() (protected val reactiveMongoApi: ReactiveMongoApi)
  extends BaseMongoRepository[User](UserCollection) with UserRepository {
  override def findByEmail(email: String): Future[Option[User]] = find(Json.obj("email" -> email)).map(_.headOption)
}
