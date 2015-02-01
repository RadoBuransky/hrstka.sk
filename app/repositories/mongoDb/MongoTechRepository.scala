package repositories.mongoDb

import models.db.Identifiable.Id
import models.db.JsonFormats._
import models.db.Tech
import repositories.TechRepository

import scala.concurrent.Future

class MongoTechRepository extends BaseMongoRepository with TechRepository {
  override def insert(name: String, author: Id): Future[Unit] =
    insert(Tech(_id = None,
      author = author,
      name = name,
      rating = None), TechCollection)
  override def all() = all(TechCollection)
}
