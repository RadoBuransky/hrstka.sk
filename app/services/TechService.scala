package services

import models.domain.Identifiable.Id
import models.domain.{User, Tech}

import scala.concurrent.Future

trait TechService {
  def insert(name: String, userId: Id): Future[Unit]
  def all(): Future[Seq[Tech]]
  def voteUp(id: Id, userId: Id): Future[Unit]
  def voteDown(id: Id, userId: Id): Future[Unit]
}
