package repositories

import com.google.inject.ImplementedBy
import models.db.City
import models.db.Identifiable.Id
import repositories.mongoDb.MongoCityRepository

import scala.concurrent.Future

@ImplementedBy(classOf[MongoCityRepository])
trait CityRepository {
  def insert(city: City): Future[Id]
  def getByHandle(handle: String): Future[City]
  def findByHandle(handle: String): Future[Option[City]]
  def all(): Future[Seq[City]]
}
