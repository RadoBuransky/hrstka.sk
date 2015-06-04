package sk.hrstka.repositories.mongoDb

import com.google.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import sk.hrstka.models.db.JsonFormats._
import sk.hrstka.models.db.{JsonFormats, Tech}
import sk.hrstka.repositories.TechRepository

@Singleton
final class MongoTechRepository @Inject() (protected val reactiveMongoApi: ReactiveMongoApi)
  extends BaseMongoRepository[Tech](TechCollection) with TechRepository {
  override def all() = find(Json.obj(), sort = Json.obj("handle" -> 1))
}