package services

import com.google.inject.ImplementedBy
import models.domain.Identifiable.Id
import models.domain.{TechRating, Handle, Tech, TechVote}
import services.impl.TechServiceImpl

import scala.concurrent.Future

@ImplementedBy(classOf[TechServiceImpl])
trait TechService {
  def get(handle: Handle): Future[Tech]
  def upsert(tech: Tech): Future[Id]
  def allRatings(): Future[Seq[TechRating]]
  def votesFor(userId: Id):Future[Seq[TechVote]]
  def voteUp(id: Id, userId: Id): Future[Unit]
  def voteDown(id: Id, userId: Id): Future[Unit]
}
