package services.impl

import models.domain.{City, CityInfo, CompQuery, Handle}
import models.db
import repositories.CityRepository
import services.LocationService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LocationServiceImpl(cityRepository: CityRepository) extends LocationService {
  override def getOrCreateCity(humanName: String): Future[City] = {
    val handle = Handle.fromHumanName(humanName)
    cityRepository.find(handle.value).map {
      case Some(city) => City(city)
      case None =>
        val newCity = db.City(
        handle  = handle.value,
        sk      = humanName)

        cityRepository.insert(newCity)
        City(newCity)
    }
  }

  override def getLocation(compQuery: CompQuery): Future[Option[String]] = ???
  override def cityInfos(): Future[Seq[CityInfo]] = ???
}
