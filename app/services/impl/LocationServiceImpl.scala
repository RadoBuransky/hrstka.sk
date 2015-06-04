package services.impl

import com.google.inject.{Inject, Singleton}
import models.db
import models.domain.{HandleFactory, City, CityFactory, Handle}
import reactivemongo.bson.BSONObjectID
import repositories.CityRepository
import services.LocationService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
final class LocationServiceImpl @Inject() (cityRepository: CityRepository) extends LocationService {
  override def all(): Future[Seq[City]] = cityRepository.all().map(_.map(CityFactory.apply))
  override def get(handle: Handle): Future[City] = cityRepository.getByHandle(handle.value).map(CityFactory.apply)
  override def getOrCreateCity(humanName: String): Future[City] = {
    val handle = HandleFactory.fromHumanName(humanName)
    cityRepository.findByHandle(handle.value).map {
      case Some(city) => CityFactory(city)
      case None =>
        val newCity = db.City(
        _id     = BSONObjectID.generate,
        handle  = handle.value,
        sk      = humanName)

        cityRepository.insert(newCity)
        CityFactory(newCity)
    }
  }
}
