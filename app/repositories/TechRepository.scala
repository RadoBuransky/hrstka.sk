package repositories

import models.db.Identifiable.Id
import models.db.Tech

import scala.concurrent.Future

trait TechRepository {
  def insert(name: String, authorId: Id): Future[Id]
  def all(): Future[Seq[Tech]]
  def get(techId: Id): Future[Tech]
  def updateRating(techId: Id, delta: Int): Future[Unit]
}
