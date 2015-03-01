package repositories

import models.db.Identifiable._

import scala.concurrent.Future

trait CompTechRepository {
  def add(authorId: Id, compId: Id, techId: Id): Future[Id]
  def remove(compId: Id, techId: Id, authorId: Id): Future[Unit]
  def getTechs(compId: Id): Future[Seq[Id]]
}
