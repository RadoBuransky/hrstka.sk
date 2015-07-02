package sk.hrstka.repositories.mongoDb

import com.google.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import sk.hrstka.common.{Logging, HrstkaCache}
import sk.hrstka.models.db.JsonFormats._
import sk.hrstka.models.db.Tech
import sk.hrstka.repositories.TechRepository

@Singleton
final class MongoTechRepository @Inject() (hrstkaCache: HrstkaCache,
                                           protected val reactiveMongoApi: ReactiveMongoApi)
  extends BaseMongoRepository[Tech](TechCollection) with TechRepository with Logging {
  override def upsert(tech: Tech) = hrstkaCache.invalidateOnSuccess(super.upsert(tech))
  override def remove(handle: String) = hrstkaCache.invalidateOnSuccess(super.remove(handle))
  override def all() = {
    logger.debug(s"all")
    find(Json.obj(), sort = Json.obj("handle" -> 1))
  }
}