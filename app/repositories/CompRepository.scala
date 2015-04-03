package repositories

import models.db.Comp
import models.db.Identifiable._

import scala.concurrent.Future

trait CompRepository {
  def get(compId: Id): Future[Comp]
  def insert(name: String, website: String, location: String, codersCount: Option[Int], femaleCodersCount: Option[Int],
             note: String, authorId: Id): Future[Id]
  def update(compId: Id, name: String, website: String, location: String, codersCount: Option[Int], femaleCodersCount: Option[Int],
             note: String): Future[Unit]
  def all(): Future[Seq[Comp]]
}
