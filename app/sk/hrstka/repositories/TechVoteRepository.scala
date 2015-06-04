package sk.hrstka.repositories

import com.google.inject.ImplementedBy
import sk.hrstka.models.db.Identifiable.Id
import sk.hrstka.models.db.TechVote
import sk.hrstka.repositories.mongoDb.MongoTechVoteRepository

import scala.concurrent.Future

@ImplementedBy(classOf[MongoTechVoteRepository])
trait TechVoteRepository {
  def vote(techId: Id, userId: Id, value: Int): Future[Boolean]
  def findValue(techId: Id, userId: Id): Future[Option[Int]]
  def all(userId: Option[Id]): Future[Iterable[TechVote]]
}