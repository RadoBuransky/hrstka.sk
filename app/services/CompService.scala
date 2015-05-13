package services

import models.domain.Identifiable._
import models.domain._

import scala.concurrent.Future

trait CompService {
  def get(compId: Id): Future[Comp]
  def upsert(comp: Comp, techNames: Seq[String], userId: Id): Future[Unit]
  def all(city: Option[Handle] = None, tech: Option[Handle] = None): Future[Seq[Comp]]
  def topCities(): Future[Seq[City]]
  def find(query: CompQuery): Future[Seq[Comp]]
}
