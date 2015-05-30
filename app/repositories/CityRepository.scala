package repositories

import com.google.inject.ImplementedBy
import models.db.City
import repositories.mongoDb.MongoCityRepository

import scala.concurrent.Future

@ImplementedBy(classOf[MongoCityRepository])
trait CityRepository {
  def all(): Future[Seq[City]]
  def get(handle: String): Future[City]
  def find(handle: String): Future[Option[City]]
  def insert(city: City): Future[String]
}
