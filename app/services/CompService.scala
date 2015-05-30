package services

import com.google.inject.ImplementedBy
import models.domain.Identifiable._
import models.domain._
import services.impl.CompServiceImpl

import scala.concurrent.Future

@ImplementedBy(classOf[CompServiceImpl])
trait CompService {
  def get(compId: Id): Future[Comp]
  def upsert(comp: Comp, techHandles: Seq[Handle], userId: Id): Future[Identifiable.Id]
  def all(city: Option[Handle] = None, tech: Option[Handle] = None): Future[Seq[Comp]]
  def topCities(): Future[Seq[City]]
  def topWomen(): Future[Seq[Comp]]
  def find(query: CompQuery): Future[Seq[Comp]]
}
