package repositories.mongoDb

import common.HEException
import models.db.City
import models.db.JsonFormats._
import play.api.libs.json.Json
import repositories.CityRepository

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class MongoCityRepository extends BaseMongoRepository(CityCollection) with CityRepository {
  override def get(handle: String): Future[City] = find[City](Json.obj("handle" -> handle)).map { cities =>
    cities.headOption match {
      case Some(city) => city
      case None => throw new HEException(s"No city found! [$handle]")
    }
  }
  override def insert(city: City): Future[String] = ins(city).map(_ => city.handle)
}
