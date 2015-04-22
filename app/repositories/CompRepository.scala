package repositories

import models.db.Comp
import models.db.Identifiable._

import scala.concurrent.Future

trait CompRepository {
  def get(compId: Id): Future[Comp]
  def upsert(comp: Comp): Future[Id]
  def all(): Future[Seq[Comp]]
}
