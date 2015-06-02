package repositories.mongoDb

import com.google.inject.{Inject, Singleton}
import models.db.Identifiable.Id
import models.db.JsonFormats._
import models.db.VoteLog
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.bson.BSONObjectID
import repositories.{CompVoteLogRepository, TechVoteLogRepository, VoteLogRepository}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

abstract class MongoVoteLogRepository(coll: MongoCollection)
  extends BaseMongoRepository[VoteLog](coll) with VoteLogRepository {
  override def logVote(id: Id, authorId: Id, value: Int): Future[Unit] =
    insert(VoteLog(BSONObjectID.generate, id, authorId, value)).map(_ => Unit)
}

@Singleton
final class MongoTechVoteLogRepository @Inject() (protected val reactiveMongoApi: ReactiveMongoApi)
  extends MongoVoteLogRepository(TechVoteLogCollection) with TechVoteLogRepository

@Singleton
final class MongoCompVoteLogRepository @Inject() (protected val reactiveMongoApi: ReactiveMongoApi)
  extends MongoVoteLogRepository(CompVoteLogCollection) with CompVoteLogRepository