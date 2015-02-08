package repositories.mongoDb

import models.db.Identifiable.Id
import models.db.JsonFormats._
import models.db.TechVote
import play.api.libs.json.Json
import play.modules.reactivemongo.json.BSONFormats._
import reactivemongo.bson.BSONObjectID
import repositories.TechVoteRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MongoTechVoteRepository extends BaseMongoRepository(TechVoteCollection) with TechVoteRepository {
  override def upVote(techId: Id, authorId: Id) = logVote(techId, authorId, TechVote.upVoteValue)
  override def downVote(techId: Id, authorId: Id) = logVote(techId, authorId, TechVote.downVoteValue)

  override def latestVote(techId: Id, authorId: Id): Future[Option[TechVote]] =
    collection
      .find(Json.obj("$and" -> Json.arr(Json.obj("techId" -> techId), Json.obj("authorId" -> authorId))))
      .sort(Json.obj("_id" -> -1))
      .one[TechVote]

  private def logVote(techId: Id, authorId: Id, value: Int): Future[Unit] = {
    insert(TechVote(_id = BSONObjectID.generate,
      techId    = techId,
      authorId  = authorId,
      value     = value)).map(techVote => ())
  }
}
