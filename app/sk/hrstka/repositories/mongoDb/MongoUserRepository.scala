package sk.hrstka.repositories.mongoDb

import com.google.inject.{Inject, Singleton}
import models.db.{JsonFormats, User}
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.bson.BSONObjectID
import reactivemongo.core.commands.LastError
import sk.hrstka.repositories.UserRepository

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import JsonFormats._

@Singleton
final class MongoUserRepository @Inject() (protected val reactiveMongoApi: ReactiveMongoApi)
  extends BaseMongoRepository[User](UserCollection) with UserRepository {
  override def findByEmail(email: String): Future[Option[User]] = find(Json.obj("email" -> email)).map(_.headOption)
}
