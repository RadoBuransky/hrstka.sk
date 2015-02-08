package repositories.mongoDb

import models.db.Identifiable.Id
import models.db.JsonFormats._
import models.db.TechVoteLog
import play.api.libs.json.Json
import play.modules.reactivemongo.json.BSONFormats._
import reactivemongo.bson.BSONObjectID
import repositories.TechVoteLogRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MongoTechVoteLogRepository extends BaseMongoRepository(TechVoteLogCollection) with TechVoteLogRepository {
  override def logUpVote(techId: Id, authorId: Id) = logVote(techId, authorId, TechVoteLog.upVoteValue)
  override def logDownVote(techId: Id, authorId: Id) = logVote(techId, authorId, TechVoteLog.downVoteValue)

  override def latestVote(techId: Id, authorId: Id): Future[Option[TechVoteLog]] =
    collection
      .find(Json.obj("$and" -> Json.arr(Json.obj("techId" -> techId), Json.obj("authorId" -> authorId))))
      .sort(Json.obj("_id" -> -1))
      .one[TechVoteLog]

  private def logVote(techId: Id, authorId: Id, value: Int): Future[Unit] = {
    insert(TechVoteLog(_id = BSONObjectID.generate,
      techId    = techId,
      authorId  = authorId,
      value     = value)).map(techVoteLog => ())
  }
}
