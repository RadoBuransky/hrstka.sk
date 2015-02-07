package repositories

import models.db.Identifiable.Id
import models.db.Tech

import scala.concurrent.Future

trait TechRepository {
  def insert(name: String, author: Id): Future[Unit]
  def all(): Future[Seq[Tech]]
  def updateRating(id: Id, delta: Int): Future[Unit]
}
