package services

import models.domain.{CityInfo, CompQuery}

import scala.concurrent.Future

trait LocationService {
  def cityInfos(): Future[Seq[CityInfo]]
  def getLocation(compQuery: CompQuery): Future[Option[String]]
}
