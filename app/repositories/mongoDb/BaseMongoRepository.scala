package repositories.mongoDb

import play.api.Play.current
import play.api.libs.json.{Json, Reads, Writes}
import play.modules.reactivemongo.ReactiveMongoPlugin
import play.modules.reactivemongo.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

abstract class BaseMongoRepository {
  protected def insert[T](value: T, coll: MongoCollection)(implicit writes: Writes[T]): Future[Unit] =
    collection(coll).insert(value).map(lastError => ())
  protected def all[T](coll: MongoCollection)(implicit reads: Reads[T]): Future[Seq[T]] =
    collection(coll).find(Json.obj()).cursor[T].collect[Seq]()

  private def collection(coll: MongoCollection) = db.collection[JSONCollection](coll.name)
  private def db = ReactiveMongoPlugin.db
}
