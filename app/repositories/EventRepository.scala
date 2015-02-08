package repositories

import models.db.Log

import scala.concurrent.Future

trait EventRepository {
  def insert(event: Log): Future[Unit]
}
