package services

import models.domain.{Handle, City, CityInfo, CompQuery}

import scala.concurrent.Future

trait LocationService {
  def get(handle: Handle): Future[City]
  def getOrCreateCity(humanName: String): Future[City]
  def cityInfos(): Future[Seq[CityInfo]]
  def getLocation(compQuery: CompQuery): Future[Option[String]]
}
