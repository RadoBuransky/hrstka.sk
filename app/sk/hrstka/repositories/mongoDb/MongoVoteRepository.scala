package sk.hrstka.repositories.mongoDb

import com.google.inject.{Inject, Singleton}
import play.api.libs.json.{Writes, Reads, Json}
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json.BSONFormats._
import reactivemongo.bson.{BSONDocument, _}
import reactivemongo.core.commands.{FindAndModify, Update}
import sk.hrstka.common.{HrstkaCache, HrstkaException}
import sk.hrstka.models.db.Identifiable.Id
import sk.hrstka.models.db._
import sk.hrstka.repositories.{CompVoteRepository, TechVoteRepository, VoteRepository}
import JsonFormats._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.reflect.ClassTag

sealed class MongoVoteRepository[TEntity <: Vote : ClassTag](hrstkaCache: HrstkaCache,
                                                             coll: MongoCollection,
                                                             protected val reactiveMongoApi: ReactiveMongoApi)
                                                            (implicit reads: Reads[TEntity], writes: Writes[TEntity])
  extends BaseMongoRepository[TEntity](coll) with VoteRepository[TEntity] {
  import MongoVoteRepository._

  override def vote(entityId: Id, userId: Id, value: Int): Future[Boolean] = {
    val findAndModify = new FindAndModify(
      collection  = collection.name,
      query       = BSONDocument(entityIdField -> entityId, userIdField -> userId),
      modify      = Update(BSONDocument(
        entityIdField -> entityId,
        userIdField -> userId,
        valueField  -> value), fetchNewObject = false),
      upsert      = true,
      sort        = Some(BSONDocument("_id" -> 1)),
      fields      = None)

    val result = db.connection.ask(findAndModify.apply(db.name).maker).map(FindAndModify(_)).map {
      case Right(Some(findAndModifyResult)) => findAndModifyResult.getAs[Int](valueField).getOrElse(value + 1) != value
      case Right(None) => true
      case Left(error) => throw new HrstkaException(s"Vote error! [$error]")
    }

    // Invalidate cache if vote is stored
    hrstkaCache.invalidateOnSuccess(result)

    result
  }

  override def findValue(entityId: Id, userId: Id): Future[Option[Int]] =
    find(Json.obj(
      entityIdField -> entityId,
      userIdField -> userId))
      .map(_.headOption.map(_.value))

  override def all(userId: Option[Id]): Future[Traversable[TEntity]] =
    userId match {
      case Some(id) => find(Json.obj(userIdField -> userId))
      case None => all()
    }
}

private object MongoVoteRepository {
  val entityIdField = "entityId"
  val userIdField = "userId"
  val valueField = "value"
}

@Singleton
final class MongoTechVoteRepository @Inject()(hrstkaCache: HrstkaCache,
                                              reactiveMongoApi: ReactiveMongoApi)
  extends MongoVoteRepository[TechVote](hrstkaCache, TechVoteCollection, reactiveMongoApi) with TechVoteRepository

@Singleton
final class MongoCompVoteRepository @Inject()(hrstkaCache: HrstkaCache,
                                              reactiveMongoApi: ReactiveMongoApi)
  extends MongoVoteRepository[CompVote](hrstkaCache, CompVoteCollection, reactiveMongoApi) with CompVoteRepository