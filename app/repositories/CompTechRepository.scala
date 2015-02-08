package repositories

import models.db.Identifiable._

import scala.concurrent.Future

trait CompTechRepository {
  def insert(authorId: Id, compId: Id, techId: Id): Future[Id]
}
