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
  override def get(compId: Id): Future[Comp] = get[Comp](compId)
  override def insert(name: String, website: String, location: String, codersCount: Option[Int], femaleCodersCount: Option[Int],
                      note: String, authorId: Id): Future[Id] = {
    val id = BSONObjectID.generate
    insert(Comp(
      _id               = id,
      name              = name,
      website           = website,
      location          = location,
      codersCount       = codersCount,
      femaleCodersCount = femaleCodersCount,
      note              = note,
      authorId          = authorId)).map(_ => id)
  }

  override def all(): Future[Seq[Comp]] = find[Comp](Json.obj())
  override def update(compId: Id, name: String, website: String, location: String, codersCount: Option[Int], femaleCodersCount: Option[Int],
                      note: String): Future[Unit] =
    super.update(compId, Json.obj(
      "name" -> name,
      "website" -> website,
      "location" -> location,
      "codersCount" -> codersCount,
      "femaleCodersCount" -> femaleCodersCount,
      "note" -> note
    ))
}
