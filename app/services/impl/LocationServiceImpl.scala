package services.impl

import models.domain.{City, CompQuery, CityInfo}
import services.LocationService

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class LocationServiceImpl extends LocationService {
  override def getOrCreateCity(humanName: String): Future[City] = {
    Future(City(
      handle  = "",
      sk      = humanName))
  }

  override def getLocation(compQuery: CompQuery): Future[Option[String]] = ???
  override def cityInfos(): Future[Seq[CityInfo]] = ???
}
