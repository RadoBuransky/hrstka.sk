package repositories.mongoDb

import com.google.inject.{Inject, Singleton}
import models.db.Identifiable.Id
import models.db.JsonFormats._
import models.db.Tech
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import repositories.TechRepository

import scala.concurrent.Future

@Singleton
final class MongoTechRepository @Inject() (protected val reactiveMongoApi: ReactiveMongoApi)
  extends BaseMongoRepository(TechCollection) with TechRepository {
  override def upsert(tech: Tech): Future[Id] = super[BaseMongoRepository].upsert(tech)
  override def all() = find[Tech](Json.obj(), sort = Json.obj("handle" -> 1))
  override def get(handle: String): Future[Tech] = super[BaseMongoRepository].get[Tech](Json.obj("handle" -> handle))
}