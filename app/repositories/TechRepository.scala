package repositories

import models.db.Tech

import scala.concurrent.Future

trait TechRepository {
  def insert(tech: Tech): Future[Unit]
  def all(): Future[Seq[Tech]]
}
