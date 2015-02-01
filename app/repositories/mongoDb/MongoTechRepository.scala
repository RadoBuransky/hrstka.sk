package repositories.mongoDb

import models.db.JsonFormats._
import models.db.Tech
import repositories.TechRepository

class MongoTechRepository extends BaseMongoRepository with TechRepository {
  override def insert(tech: Tech) = insert(tech, TechCollection)
  override def all() = all(TechCollection)
}
