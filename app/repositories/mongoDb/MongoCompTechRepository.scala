package repositories.mongoDb

import models.db.{Tech, CompTech}
import models.db.Identifiable.Id
import models.db.JsonFormats._
import reactivemongo.bson.BSONObjectID
import repositories.CompTechRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MongoCompTechRepository extends BaseMongoRepository(CompTechCollection) with CompTechRepository {
  override def add(authorId: Id, compId: Id, techId: Id): Future[Id] = {
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

  override def del(compTechId: Id, authorId: Id): Unit = ???

  override def getTechs(compId: Id): Future[Seq[Tech]] = ???

  override def getUnassignedTechs(compId: Id): Future[Seq[Tech]] = ???
}