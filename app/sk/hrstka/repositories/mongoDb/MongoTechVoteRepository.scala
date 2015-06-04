package sk.hrstka.repositories.mongoDb

import com.google.inject.{Inject, Singleton}
import common.HrstkaException
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json.BSONFormats._
import reactivemongo.bson.{BSONDocument, _}
import reactivemongo.core.commands.{FindAndModify, Update}
import sk.hrstka.models.db.Identifiable.Id
import sk.hrstka.models.db.JsonFormats._
import sk.hrstka.models.db.{Identifiable, JsonFormats, TechVote}
import sk.hrstka.repositories.TechVoteRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
final class MongoTechVoteRepository @Inject()(protected val reactiveMongoApi: ReactiveMongoApi)
  extends BaseMongoRepository[TechVote](TechVoteCollection) with TechVoteRepository {
  import MongoTechVoteRepository._

  override def vote(techId: Id, userId: Id, value: Int): Future[Boolean] = {
    val findAndModify = new FindAndModify(
      collection  = collection.name,
      query       = BSONDocument(techIdField -> techId, userIdField -> userId),
      modify      = Update(BSONDocument(
        techIdField -> techId,
        userIdField -> userId,
        valueField  -> value), fetchNewObject = false),
      upsert      = true,
      sort        = Some(BSONDocument("_id" -> 1)),
      fields      = None)
    db.connection.ask(findAndModify.apply(db.name).maker).map(FindAndModify(_)).map {
      case Right(Some(findAndModifyResult)) => findAndModifyResult.getAs[Int](valueField).getOrElse(value + 1) != value
      case Right(None) => true
      case Left(error) => throw new HrstkaException(s"Tech vote error! [$error]")
    }
  }

  override def findValue(techId: Id, userId: Id): Future[Option[Int]] =
    find(Json.obj(
      techIdField -> techId,
      userIdField -> userId))
      .map(_.headOption.map(_.value))

  override def all(userId: Option[Id]): Future[Seq[TechVote]] =
    userId match {
      case Some(id) => find(Json.obj(userIdField -> userId))
      case None => all()
    }
}

private object MongoTechVoteRepository {
  val techIdField = "techId"
  val userIdField = "userId"
  val valueField = "value"
}