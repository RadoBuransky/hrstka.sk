package services

import java.net.URL

import models.domain.{CompQuery, Comp}
import models.domain.Identifiable._

import scala.concurrent.Future

trait CompService {
  def get(compId: Id): Future[Comp]
  def upsert(comp: Comp, techNames: Seq[String], userId: Id): Future[Unit]
  def all(): Future[Seq[Comp]]

  def find(query: CompQuery, location: Option[String], tech: Option[String]): Future[Seq[Comp]]
}
