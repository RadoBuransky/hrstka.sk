package repositories.mongoDb

import common.HEException
import models.db.Identifiable.Id
import models.db.Vote
import play.api.libs.json.Json
import reactivemongo.bson.{BSONDocument, _}
import reactivemongo.core.commands.{FindAndModify, Update}
import repositories.VoteRepository
import models.db.JsonFormats._

import play.modules.reactivemongo.json.BSONFormats._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MongoVoteRepository(coll: MongoCollection) extends BaseMongoRepository(coll) with VoteRepository {
  override def vote(id: Id, authorId: Id, value: Int): Future[Boolean] = {
    val findAndModify = new FindAndModify(
      collection  = coll.name,
      query       = BSONDocument("id" -> id, "authorId" -> authorId),
      modify      = Update(BSONDocument(
        "id"          -> id,
        "authorId"    -> authorId,
        "value"       -> value), fetchNewObject = false),
      upsert      = true,
      sort        = Some(BSONDocument("_id" -> 1)),
      fields      = None)
    db.connection.ask(findAndModify.apply(db.name).maker).map(FindAndModify(_)).map {
      case Right(Some(findAndModifyResult)) => findAndModifyResult.getAs[Int]("value").getOrElse(value + 1) != value
      case Right(None) => true
      case Left(error) => throw new HEException(s"Log vote error! [$error]")
    }
  }

  override def getValue(id: Id, authorId: Id): Future[Option[Int]] =
    find[Vote](Json.obj("id" -> id, "authorId" -> authorId)).map(_.headOption.map(_.value))
}

object MongoTechVoteRepository extends MongoVoteRepository(TechVoteCollection)
object MongoCompVoteRepository extends MongoVoteRepository(CompVoteCollection)
