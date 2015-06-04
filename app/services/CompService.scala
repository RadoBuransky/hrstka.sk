package services

import com.google.inject.ImplementedBy
import models.domain.Identifiable._
import models.domain._
import services.impl.CompServiceImpl

import scala.concurrent.Future

@ImplementedBy(classOf[CompServiceImpl])
trait CompService {
  def upsert(comp: Comp, techHandles: Set[Handle], userId: Id): Future[Identifiable.Id]
  def get(compId: Id): Future[Comp]
  def all(city: Option[Handle] = None, tech: Option[Handle] = None): Future[Seq[Comp]]
  def topWomen(): Future[Seq[Comp]]
}
