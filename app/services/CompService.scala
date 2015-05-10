package services

import models.domain.Identifiable._
import models.domain.{City, Comp, CompQuery}

import scala.concurrent.Future

trait CompService {
  def get(compId: Id): Future[Comp]
  def upsert(comp: Comp, techNames: Seq[String], userId: Id): Future[Unit]
  def all(): Future[Seq[Comp]]
  def topCities(): Future[Seq[City]]

  def find(query: CompQuery, location: Option[String], tech: Option[String]): Future[Seq[Comp]]
}
