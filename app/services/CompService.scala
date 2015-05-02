package services

import java.net.URL

import models.domain.{CompQuery, Comp}
import models.domain.Identifiable._

import scala.concurrent.Future

trait CompService {
  def get(compId: Id): Future[Comp]
  def insert(name: String,
             website: URL,
             location: String,
             employeeCount: Option[Int],
             codersCount: Option[Int],
             femaleCodersCount: Option[Int],
             note: String,
             userId: Id,
             products: Boolean,
             services: Boolean,
             internal: Boolean,
             techNames: Seq[String], joel: Set[Int]): Future[Id]
  def update(comp: Comp, techNames: Seq[String], userId: Id): Future[Unit]
  def all(): Future[Seq[Comp]]

  def find(query: CompQuery, location: Option[String], tech: Option[String]): Future[Seq[Comp]]
}
