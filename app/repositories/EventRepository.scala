package repositories

import models.db.Event

import scala.concurrent.Future

trait EventRepository {
  def insert(event: Event): Future[Unit]
}
