package sk.hrstka.repositories.mongoDb

import com.google.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import sk.hrstka.common.{HrstkaCache, HrstkaException, Logging}
import sk.hrstka.models.db.Comp
import sk.hrstka.models.db.Identifiable._
import sk.hrstka.models.db.JsonFormats._
import sk.hrstka.repositories.CompRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
final class MongoCompRepository @Inject() (hrstkaCache: HrstkaCache,
                                           protected val reactiveMongoApi: ReactiveMongoApi)
  extends BaseMongoRepository[Comp](CompCollection) with CompRepository with Logging {

  override def upsert(comp: Comp): Future[Id] = hrstkaCache.invalidateOnSuccess(super.upsert(comp))

  override def get(businessNumber: String): Future[Comp] = find(Json.obj("businessNumber" -> businessNumber)).map { comps =>
    if (comps.isEmpty)
      throw new HrstkaException(s"No company exists for the business number! [$businessNumber]")
    comps.head
  }

  override def all(city: Option[Handle] = None, tech: Option[Handle] = None): Future[Iterable[Comp]] = {
    logger.debug(s"all [$city, $tech]")
    val cityQuery = city match {
      case Some(cityHandle) => Json.obj("city" -> cityHandle)
      case None => Json.obj()
    }

    val techQuery = tech match {
      case Some(techHandle) => Json.obj("techs" -> Json.obj("$in" -> Json.arr(techHandle)))
      case None => Json.obj()
    }

    find(cityQuery ++ techQuery)
  }
}
