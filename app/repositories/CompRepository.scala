package repositories

import models.db.Comp
import models.db.Identifiable._

import scala.concurrent.Future

trait CompRepository {
  def get(compId: Id): Future[Comp]
  def insert(name: String, website: String, location: String, codersCount: Option[Int], femaleCodersCount: Option[Int],
             note: String): Future[Id]
  def update(comp: Comp): Future[Unit]
  def all(): Future[Seq[Comp]]
}
