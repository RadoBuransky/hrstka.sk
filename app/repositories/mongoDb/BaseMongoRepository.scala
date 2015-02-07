package repositories.mongoDb

import models.db.Identifiable.Id
import play.api.Play.current
import play.api.libs.json.{JsValue, Json, Reads, Writes}
import play.modules.reactivemongo.ReactiveMongoPlugin
import play.modules.reactivemongo.json.collection.JSONCollection
import play.modules.reactivemongo.json.BSONFormats._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

abstract class BaseMongoRepository(coll: MongoCollection) {
  protected def insert[T](value: T)(implicit writes: Writes[T]): Future[Unit] =
    collection.insert(value).map(lastError => ())

  protected def update(id: Id, value: JsValue): Future[Unit] =
    collection.update(Json.obj("_id" -> id), value).map(lastError => ())

  protected def all[T]()(implicit reads: Reads[T]): Future[Seq[T]] =
    collection.find(Json.obj()).cursor[T].collect[Seq]()

  protected def collection = db.collection[JSONCollection](coll.name)

  private def db = ReactiveMongoPlugin.db
}
