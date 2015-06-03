package repositories

import com.google.inject.ImplementedBy
import models.db.Identifiable.Id
import models.db.TechVote
import repositories.mongoDb.MongoTechVoteRepository

import scala.concurrent.Future

@ImplementedBy(classOf[MongoTechVoteRepository])
trait TechVoteRepository {
  def vote(techId: Id, userId: Id, value: Int): Future[Boolean]
  def findValue(techId: Id, userId: Id): Future[Option[Int]]
  def all(userId: Id): Future[Seq[TechVote]]
}