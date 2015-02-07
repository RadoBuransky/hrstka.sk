package repositories.mongoDb

import models.db.Identifiable.Id
import models.db.JsonFormats._
import models.db.Tech
import play.api.libs.json.Json
import repositories.TechRepository
import repositories.mongoDb.MongoOperators._

import scala.concurrent.Future

class MongoTechRepository extends BaseMongoRepository(TechCollection) with TechRepository {
  override def insert(name: String, author: Id): Future[Unit] =
    insert(Tech(_id = None,
      author = author,
      name = name,
      upVotes = 0,
      downVotes = 0))

  override def all() = super.all[Tech]()

  override def updateRating(id: Id, delta: Int) = {
    val field = if (delta < 0) "downVotes" else "upVotes"
    update(id, Json.obj(inc -> Json.obj(field -> 1)))
  }
}
