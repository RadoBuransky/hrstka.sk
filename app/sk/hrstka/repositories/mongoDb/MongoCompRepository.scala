package sk.hrstka.repositories.mongoDb

import com.google.inject.{ImplementedBy, Inject, Singleton}
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import sk.hrstka.common.{HrstkaCache, HrstkaException, Logging}
import sk.hrstka.models.db.Comp
import sk.hrstka.models.db.Identifiable._
import sk.hrstka.models.db.JsonFormats._
import sk.hrstka.repositories.CompRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Marker trait.
 */
@ImplementedBy(classOf[MongoCompRepository])
private[mongoDb] trait NotCachedCompRepository extends CompRepository

@Singleton
final class MongoCompRepository @Inject() (hrstkaCache: HrstkaCache,
                                           protected val reactiveMongoApi: ReactiveMongoApi)
  extends BaseMongoRepository[Comp](CompCollection) with NotCachedCompRepository with Logging {

  override def upsert(comp: Comp): Future[Id] = hrstkaCache.invalidateOnSuccess(super.upsert(comp))

  override def get(businessNumber: String): Future[Comp] = find(Json.obj("businessNumber" -> businessNumber)).map { comps =>
    if (comps.isEmpty)
      throw new HrstkaException(s"No company exists for the business number! [$businessNumber]")
    comps.head
  }
}
