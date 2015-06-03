package repositories.mongoDb

import com.google.inject.{Inject, Singleton}
import models.db.Comp
import models.db.Identifiable._
import models.db.JsonFormats._
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import repositories.CompRepository

import scala.concurrent.Future

@Singleton
final class MongoCompRepository @Inject() (protected val reactiveMongoApi: ReactiveMongoApi)
  extends BaseMongoRepository[Comp](CompCollection) with CompRepository {
  override def all(city: Option[Handle] = None, tech: Option[Handle] = None): Future[Seq[Comp]] = {
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
