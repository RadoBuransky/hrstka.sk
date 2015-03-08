package services

import models.domain.Identifiable.Id
import models.domain.{TechVote, User, Tech}

import scala.concurrent.Future

trait TechService {
  def insert(name: String, userId: Id): Future[Id]
  def all(): Future[Seq[Tech]]
  def votesFor(userId: Id):Future[Seq[TechVote]]
  def voteUp(id: Id, userId: Id): Future[Unit]
  def voteDown(id: Id, userId: Id): Future[Unit]
}
