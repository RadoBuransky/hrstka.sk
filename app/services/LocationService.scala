package services

import models.domain.{Handle, City, CityInfo, CompQuery}

import scala.concurrent.Future

trait LocationService {
  def all(): Future[Seq[City]]
  def get(handle: Handle): Future[City]
  def getOrCreateCity(humanName: String): Future[City]
  def getLocation(compQuery: CompQuery): Future[Option[String]]
}
