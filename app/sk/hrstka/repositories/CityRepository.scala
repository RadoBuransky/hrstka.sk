package sk.hrstka.repositories

import com.google.inject.ImplementedBy
import sk.hrstka.models.db.City
import sk.hrstka.models.db.Identifiable.Id
import sk.hrstka.repositories.mongoDb.MongoCityRepository

import scala.concurrent.Future

@ImplementedBy(classOf[MongoCityRepository])
trait CityRepository {
  def insert(city: City): Future[Id]
  def getByHandle(handle: String): Future[City]
  def findByHandle(handle: String): Future[Option[City]]
  def all(): Future[Seq[City]]
}
