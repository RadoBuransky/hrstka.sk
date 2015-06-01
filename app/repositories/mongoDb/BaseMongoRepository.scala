package repositories.mongoDb

import common.HEException
import models.db.Identifiable
import models.db.Identifiable.Id
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json.BSONFormats._
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.QueryOpts
import reactivemongo.core.commands.LastError

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.reflect.ClassTag

abstract class BaseMongoRepository(coll: MongoCollection) {
  protected def reactiveMongoApi: ReactiveMongoApi

  protected def ins[T](value: T)(implicit writes: Writes[T]): Future[LastError] = {
    collection.insert(value)
  }

  protected def update(id: Id, value: JsValue): Future[Unit] =
    collection.update(Json.obj("_id" -> id), value).map(lastError => ())

  protected def update[T <: Identifiable](value: T)(implicit writes: Writes[T]): Future[Unit] =
    collection.update(Json.obj("_id" -> value._id), value).map(lastError => ())

  protected def upsert[T <: Identifiable : ClassTag](value: T)(implicit writes: Writes[T]): Future[Id] = {
    collection.update(Json.obj("_id" -> value._id), value, upsert = true).recover {
      case ex: Exception => throw new HEException(s"Could not upsert ${coll.name}! [$value]", ex)
    }.map(lastError => value._id)
  }

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

  protected def get[T](id: Id)(implicit reads: Reads[T]): Future[T] =  find[T](Json.obj("_id" -> id)).map {
    _.headOption match {
      case Some(result) => result
      case None => throw new HEException(s"No ${coll.name} with _id = ${id.stringify}!")
    }
  }

  protected def remove(id: Id): Future[LastError] = collection.remove(Json.obj("_id" -> id))

  protected def collection = db.collection[JSONCollection](coll.name)
  protected def db = reactiveMongoApi.db
}
