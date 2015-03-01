package repositories.mongoDb

import models.db.Comp
import models.db.Identifiable.Id
import play.api.libs.json.Json
import reactivemongo.bson.BSONObjectID
import repositories.CompRepository
import models.db.JsonFormats._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MongoCompRepository extends BaseMongoRepository(CompCollection) with CompRepository {
  override def insert(name: String, website: String, authorId: Id): Future[Id] = {
    val id = BSONObjectID.generate
    insert(Comp(_id = id,
      authorId = authorId,
      name = name,
      website = website,
      upVotes = 0,
      downVotes = 0)).map(_ => id)
  }

  override def all(): Future[Seq[Comp]] = find[Comp](Json.obj())
}
