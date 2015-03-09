package repositories.mongoDb

import models.db.Identifiable.Id
import models.db.JsonFormats._
import models.db.Tech
import play.api.Logger
import play.api.libs.json.Json
import reactivemongo.bson.BSONObjectID
import repositories.TechRepository
import repositories.mongoDb.MongoOperators._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MongoTechRepository extends BaseMongoRepository(TechCollection) with TechRepository {
  import MongoTechRepository._

  override def insert(name: String, authorId: Id): Future[Id] = {
    val id = BSONObjectID.generate
    insert(Tech(_id = id,
      authorId = authorId,
      name = name,
      upVotes = 0,
      upVotesValue = 0,
      downVotes = 0)).map(_ => id)
  }

  override def all() = find[Tech](Json.obj())

  override def updateRating(techId: Id, delta: Int, value: Int) = {
    if (delta != -1 && delta != 1)
      throw new IllegalArgumentException(s"Delta must be 1 or -1! [$delta]")

    val field =
      if (value < 0)
        downVotes
      else
        if (value == 0)
          if (delta == -1)
            upVotesValue
          else
            downVotes
        else
          upVotesValue

    val realDelta =
      if (field == upVotesValue)
        delta
      else
        -1 * delta

    val valueCommand = Json.obj(inc -> Json.obj(field -> realDelta))
    val upVotesCommand =
      if (field == upVotesValue && ((value == 1 && delta == 1) || (value < 1 && delta == -1)))
        Json.obj(inc -> Json.obj(upVotes -> delta))
      else
        Json.obj()

    update(techId, valueCommand deepMerge upVotesCommand)
  }

  override def get(techId: Id): Future[Tech] = get[Tech](techId)
}

object MongoTechRepository {
  val downVotes = "downVotes"
  val upVotes = "upVotes"
  val upVotesValue = "upVotesValue"
}