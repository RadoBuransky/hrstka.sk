package repositories.mongoDb

import models.db.Identifiable.Id
import play.api.Play.current
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoPlugin
import play.modules.reactivemongo.json.collection.JSONCollection
import play.modules.reactivemongo.json.BSONFormats._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

abstract class BaseMongoRepository(coll: MongoCollection) {
  protected def insert[T](value: T)(implicit writes: Writes[T]): Future[T] =
    collection.insert(value).map(lastError => value)

  protected def update(id: Id, value: JsValue): Future[Unit] =
    collection.update(Json.obj("_id" -> id), value).map(lastError => ())

  protected def all[T]()(implicit reads: Reads[T]) = find[T](Json.obj())

  protected def find[T](selector: JsValue, sort: JsValue = JsNull)(implicit reads: Reads[T]): Future[Seq[T]] = {
    val findResult = collection.find(selector)
    val sortResult = sort match {
      case sortValue: JsObject => findResult.sort(sortValue)
      case _ => findResult
    }
    sortResult.cursor[T].collect[Seq]()
  }

  protected def collection = db.collection[JSONCollection](coll.name)

  private def db = ReactiveMongoPlugin.db
}
