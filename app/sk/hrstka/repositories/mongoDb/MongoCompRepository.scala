package sk.hrstka.repositories.mongoDb

import com.google.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import sk.hrstka.common.Logging
import sk.hrstka.models.db.Identifiable._
import sk.hrstka.models.db.JsonFormats._
import sk.hrstka.models.db.{Comp, Identifiable, JsonFormats}
import sk.hrstka.repositories.CompRepository

import scala.concurrent.Future

@Singleton
final class MongoCompRepository @Inject() (protected val reactiveMongoApi: ReactiveMongoApi)
  extends BaseMongoRepository[Comp](CompCollection) with CompRepository with Logging {
  override def all(city: Option[Handle] = None, tech: Option[Handle] = None): Future[Iterable[Comp]] = {
    logger.info(s"all [$city, $tech]")

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
