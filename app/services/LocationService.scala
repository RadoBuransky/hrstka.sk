package services

import models.domain.{City, CityInfo, CompQuery}

import scala.concurrent.Future

trait LocationService {
  def getOrCreateCity(humanName: String): Future[City]
  def cityInfos(): Future[Seq[CityInfo]]
  def getLocation(compQuery: CompQuery): Future[Option[String]]
}
