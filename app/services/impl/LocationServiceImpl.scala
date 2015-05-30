package services.impl

import com.google.inject.{Inject, Singleton}
import models.db
import models.domain.{City, CompQuery, Handle}
import reactivemongo.bson.BSONObjectID
import repositories.CityRepository
import services.LocationService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
final class LocationServiceImpl @Inject() (cityRepository: CityRepository) extends LocationService {
  override def all(): Future[Seq[City]] = cityRepository.all().map(_.map(City.apply))
  override def get(handle: Handle): Future[City] = cityRepository.get(handle.value).map(City.apply)
  override def getOrCreateCity(humanName: String): Future[City] = {
    val handle = Handle.fromHumanName(humanName)
    cityRepository.find(handle.value).map {
      case Some(city) => City(city)
      case None =>
        val newCity = db.City(
        _id     = BSONObjectID.generate,
        handle  = handle.value,
        sk      = humanName)

        cityRepository.insert(newCity)
        City(newCity)
    }
  }

  override def getLocation(compQuery: CompQuery): Future[Option[String]] = ???
}
