package services

import com.google.inject.ImplementedBy
import models.domain.{City, Handle}
import services.impl.LocationServiceImpl

import scala.concurrent.Future

@ImplementedBy(classOf[LocationServiceImpl])
trait LocationService {
  def all(): Future[Seq[City]]
  def get(handle: Handle): Future[City]
  def getOrCreateCity(humanName: String): Future[City]
}
