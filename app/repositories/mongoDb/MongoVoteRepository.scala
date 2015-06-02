package repositories.mongoDb

import com.google.inject.{Inject, Singleton}
import common.HEException
import models.db.Identifiable.Id
import models.db.JsonFormats._
import models.db.Vote
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json.BSONFormats._
import reactivemongo.bson.{BSONDocument, _}
import reactivemongo.core.commands.{FindAndModify, Update}
import repositories.{CompVoteRepository, TechVoteRepository, VoteRepository}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

abstract class MongoVoteRepository(coll: MongoCollection)
  extends BaseMongoRepository[Vote](coll) with VoteRepository {
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
    find(Json.obj("id" -> id, "authorId" -> authorId)).map(_.headOption.map(_.value))

  override def getAll(authorId: Id): Future[Seq[Vote]] =
    find(Json.obj("authorId" -> authorId))
}

@Singleton
final class MongoTechVoteRepository @Inject()(protected val reactiveMongoApi: ReactiveMongoApi)
  extends MongoVoteRepository(TechVoteCollection) with TechVoteRepository

@Singleton
final class MongoCompVoteRepository @Inject() (protected val reactiveMongoApi: ReactiveMongoApi)
  extends MongoVoteRepository(CompVoteCollection) with CompVoteRepository
