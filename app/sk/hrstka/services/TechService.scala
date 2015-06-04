package sk.hrstka.services

import com.google.inject.ImplementedBy
import sk.hrstka.models.domain.Identifiable.Id
import sk.hrstka.models.domain._
import sk.hrstka.services.impl.TechServiceImpl

import scala.concurrent.Future

@ImplementedBy(classOf[TechServiceImpl])
trait TechService {
  def get(handle: Handle): Future[Tech]
  def upsert(tech: Tech): Future[Id]
  def allRatings(): Future[Seq[TechRating]]
  def votesFor(userId: Id):Future[Iterable[TechVote]]
  def voteUp(id: Id, userId: Id): Future[Unit]
  def voteDown(id: Id, userId: Id): Future[Unit]
  def allCategories(): Future[Seq[TechCategory]]
}
