package repositories

import com.google.inject.ImplementedBy
import models.db.Identifiable._
import repositories.mongoDb.{MongoCompVoteLogRepository, MongoTechVoteLogRepository}

import scala.concurrent.Future

trait VoteLogRepository {
  def logVote(id: Id, authorId: Id, value: Int): Future[Unit]
}

@ImplementedBy(classOf[MongoTechVoteLogRepository])
trait TechVoteLogRepository extends VoteLogRepository

@ImplementedBy(classOf[MongoCompVoteLogRepository])
trait CompVoteLogRepository extends VoteLogRepository