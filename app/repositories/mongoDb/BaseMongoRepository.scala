package repositories.mongoDb

import common.HEException
import models.db.Identifiable
import models.db.Identifiable.Id
import play.api.Logger
import play.api.Play.current
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoPlugin
import play.modules.reactivemongo.json.collection.JSONCollection
import play.modules.reactivemongo.json.BSONFormats._
import reactivemongo.api.QueryOpts
import reactivemongo.core.commands.{LastError, CollStatsResult, CollStats}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

abstract class BaseMongoRepository(coll: MongoCollection) {
  protected def insert[T](value: T)(implicit writes: Writes[T]): Future[LastError] = collection.insert(value)

  protected def update(id: Id, value: JsValue): Future[Unit] =
    collection.update(Json.obj("_id" -> id), value).map(lastError => ())

  protected def update[T <: Identifiable](value: T)(implicit writes: Writes[T]): Future[Unit] =
    collection.update(Json.obj("_id" -> value._id), value).map(lastError => ())

  protected def find[T](selector: JsValue, sort: JsValue = JsNull, first: Boolean = false)(implicit reads: Reads[T]): Future[Seq[T]] = {
    val findResult = collection.find(selector)
    val sortResult = sort match {
      case sortValue: JsObject => findResult.sort(sortValue)
      case _ => findResult
    }

    val batch = if (first)
      sortResult.options(QueryOpts(batchSizeN = 1))
    else
      sortResult

    batch.cursor[T].collect[Seq]()
  }

  protected def get[T](id: Id)(implicit reads: Reads[T]): Future[T] =  find[T](Json.obj("_id" -> id)).map(_.head)

  protected def remove(id: Id): Future[LastError] = collection.remove(Json.obj("_id" -> id))

  protected def collCount(): Future[Int] = {
    val collStats = new CollStats(CompTechCollection.name)
    db.connection.ask(collStats(db.name).maker).map(CollStatsResult(_)).map {
      case Right(collStatsResult) => collStatsResult.count
      case Left(error) => {
        Logger.error(s"Collection stats error! [$coll]", error)
        0
      }
    }
  }

  protected def collection = db.collection[JSONCollection](coll.name)
  protected def db = ReactiveMongoPlugin.db
}
