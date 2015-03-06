package repositories.mongoDb

import models.db.Identifiable.Id
import models.db.JsonFormats._
import models.db.VoteLog
import reactivemongo.bson.BSONObjectID
import repositories.VoteLogRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MongoVoteLogRepository(coll: MongoCollection) extends BaseMongoRepository(coll) with VoteLogRepository {
  override def logVote(id: Id, authorId: Id, value: Int): Future[Unit] =
    insert(VoteLog(BSONObjectID.generate, id, authorId, value)).map(_ => Unit)
}

object MongoTechVoteLogRepository extends MongoVoteLogRepository(TechVoteLogCollection)
object MongoCompVoteLogRepository extends MongoVoteLogRepository(CompVoteLogCollection)