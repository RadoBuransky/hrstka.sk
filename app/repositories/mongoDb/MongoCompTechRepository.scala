package repositories.mongoDb

import models.db.CompTech
import models.db.Identifiable.Id
import models.db.JsonFormats._
import reactivemongo.bson.BSONObjectID
import repositories.CompTechRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MongoCompTechRepository extends BaseMongoRepository(CompTechCollection) with CompTechRepository {
  override def insert(authorId: Id, compId: Id, techId: Id): Future[Id] = {
    collCount().flatMap { count =>
      insert(CompTech(
        _id       = BSONObjectID.generate,
        authorId  = authorId,
        compId    = compId,
        techId    = techId,
        index     = count,
        removed   = None)).map(_._id)
    }
  }
}