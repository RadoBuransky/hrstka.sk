package services

import com.google.inject.ImplementedBy
import models.domain.Identifiable.Id
import models.domain.{Handle, Tech, TechVote}
import services.impl.TechServiceImpl

import scala.concurrent.Future

@ImplementedBy(classOf[TechServiceImpl])
trait TechService {
  def get(handle: Handle): Future[Tech]
  def insert(name: String, userId: Id): Future[Id]
  def getOrInsert(name: String, userId: Id): Future[Id]
  def all(): Future[Seq[Tech]]
  def topTechs(): Future[Seq[Tech]]
  def votesFor(userId: Id):Future[Seq[TechVote]]
  def voteUp(id: Id, userId: Id): Future[Unit]
  def voteDown(id: Id, userId: Id): Future[Unit]
}
