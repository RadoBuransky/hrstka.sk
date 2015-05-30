package repositories

import com.google.inject.ImplementedBy
import models.db.Identifiable.Id
import models.db.Vote
import repositories.mongoDb.{MongoCompVoteRepository, MongoTechVoteRepository}

import scala.concurrent.Future

trait VoteRepository {
  def vote(id: Id, authorId: Id, value: Int): Future[Boolean]
  def getValue(id: Id, authorId: Id): Future[Option[Int]]
  def getAll(authorId: Id): Future[Seq[Vote]]
}

@ImplementedBy(classOf[MongoCompVoteRepository])
trait CompVoteRepository extends VoteRepository

@ImplementedBy(classOf[MongoTechVoteRepository])
trait TechVoteRepository extends VoteRepository