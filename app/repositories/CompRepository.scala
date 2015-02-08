package repositories

import models.db.Comp
import models.db.Identifiable._

import scala.concurrent.Future

trait CompRepository {
  def insert(name: String, website: String, authorId: Id): Future[Id]
  def all(): Future[Seq[Comp]]
}
