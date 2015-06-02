package repositories.mongoDb

import com.google.inject.{Inject, Singleton}
import common.HEException
import models.db.City
import models.db.JsonFormats._
import org.slf4j.LoggerFactory
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import repositories.CityRepository

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
final class MongoCityRepository @Inject() (protected val reactiveMongoApi: ReactiveMongoApi)
  extends BaseMongoRepository(CityCollection) with CityRepository {
  override def get(handle: String): Future[City] = find(handle).map {
    case Some(city) => city
    case None => throw new HEException(s"No city found! [$handle]")
  }

  override def find(handle: String): Future[Option[City]] = find[City](Json.obj("handle" -> handle)).map(_.headOption)
  override def insert(city: City): Future[String] = ins(city).map(_ => city.handle)
  override def all(): Future[Seq[City]] = find[City](Json.obj())
}
