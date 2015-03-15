package repositories

import models.db.Comp
import models.db.Identifiable._

import scala.concurrent.Future

trait CompRepository {
  def insert(name: String, website: String, location: String, codersCount: Option[Int], femaleCodersCount: Option[Int],
             note: String): Future[Id]
  def all(): Future[Seq[Comp]]
}
