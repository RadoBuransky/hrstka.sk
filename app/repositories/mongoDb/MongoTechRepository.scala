package repositories.mongoDb

import models.db.Identifiable.Id
import models.db.JsonFormats._
import models.db.Tech
import play.api.libs.json.Json
import reactivemongo.bson.BSONObjectID
import repositories.TechRepository
import repositories.mongoDb.MongoOperators._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MongoTechRepository extends BaseMongoRepository(TechCollection) with TechRepository {

  override def insert(name: String, authorId: Id): Future[Id] = {
    val id = BSONObjectID.generate
    insert(Tech(_id = id,
      authorId = authorId,
      name = name,
      upVotes = 0,
      downVotes = 0)).map(_ => id)
  }

  override def all() = find[Tech](Json.obj())

  override def updateRating(techId: Id, delta: Int) = {
    val field = if (delta < 0) "downVotes" else "upVotes"
    update(techId, Json.obj(inc -> Json.obj(field -> Math.abs(delta))))
  }

  override def get(techId: Id): Future[Tech] = get[Tech](techId)
}