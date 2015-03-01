package repositories.mongoDb

import common.HEException
import models.db.Identifiable.Id
import play.api.Play.current
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoPlugin
import play.modules.reactivemongo.json.collection.JSONCollection
import play.modules.reactivemongo.json.BSONFormats._
import reactivemongo.core.commands.{LastError, CollStatsResult, CollStats}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

abstract class BaseMongoRepository(coll: MongoCollection) {
  protected def insert[T](value: T)(implicit writes: Writes[T]): Future[LastError] = collection.insert(value)

  protected def update(id: Id, value: JsValue): Future[Unit] =
    collection.update(Json.obj("_id" -> id), value).map(lastError => ())

  protected def find[T](selector: JsValue, sort: JsValue = JsNull)(implicit reads: Reads[T]): Future[Seq[T]] = {
    val findResult = collection.find(selector)
    val sortResult = sort match {
      case sortValue: JsObject => findResult.sort(sortValue)
      case _ => findResult
    }
    sortResult.cursor[T].collect[Seq]()
  }

  protected def get[T](id: Id)(implicit reads: Reads[T]): Future[T] =  find[T](Json.obj("_id" -> id)).map(_.head)

  protected def remove(id: Id): Future[LastError] = collection.remove(Json.obj("_id" -> id))

  protected def collCount(): Future[Int] = {
    val collStats = new CollStats(CompTechCollection.name)
    db.connection.ask(collStats(db.name).maker).map(CollStatsResult(_)).map {
      case Right(collStatsResult) => collStatsResult.count
      case Left(error) => throw new HEException(s"Collection stats error! [$error]")
    }
  }

  protected def collection = db.collection[JSONCollection](coll.name)
  protected def db = ReactiveMongoPlugin.db
}
