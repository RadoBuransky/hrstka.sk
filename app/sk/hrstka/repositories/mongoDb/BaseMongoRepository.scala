package sk.hrstka.repositories.mongoDb

import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json.BSONFormats._
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.QueryOpts
import reactivemongo.bson.BSONObjectID
import sk.hrstka.common.HrstkaException
import sk.hrstka.models.db.Identifiable
import sk.hrstka.models.db.Identifiable.Id

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.reflect.ClassTag

/**
 * Common repository operations.
 *
 * @param coll MongoDB collection.
 * @param ev1 Scala type evidence.
 * @param reads Json reads.
 * @param writes Json writes.
 * @tparam T Entity type.
 */
abstract class BaseMongoRepository[T <: Identifiable : ClassTag](coll: MongoCollection)(implicit reads: Reads[T], writes: Writes[T]) {
  protected def reactiveMongoApi: ReactiveMongoApi
  /**
   * Inserts new entity. Generates identifier for it.
   *
   * @param value Entity to insert.
   * @return Entity identifier.
   */
  def insert(value: T): Future[Id] = {
    if (value._id != Identifiable.empty)
      throw new HrstkaException(s"Value to be inserted cannot have ID set! [$value]")
    val id = BSONObjectID.generate
    collection.insert(Json.toJson(value).as[JsObject] ++ Json.obj("_id" -> id)).recover {
      case ex: Exception => throw new HrstkaException(s"Could not insert ${coll.name}! [$value]", ex)
    }.map(_ => id)
  }

   /**
   * Updates or inserts provided entity. Generates identifier if empty.
   *
   * @param value Entity to upsert.
   * @return Entity identifier.
   */
  def upsert(value: T): Future[Id] = {
     value._id match {
       case Identifiable.empty => insert(value)
       case _ =>
         collection.update(Json.obj("_id" -> value._id), value, upsert = true).recover {
           case ex: Exception => throw new HrstkaException(s"Could not upsert ${coll.name}! [$value]", ex)
         }.map(_ => value._id)
     }
  }

  /**
   * Removes entity with the provided identifier.
   *
   * @param id Entity identifier.
   * @return Identifier of removed entity.
   */
  def remove(id: Id): Future[Id] = collection.remove(Json.obj("_id" -> id)).map(_ => id)

  /**
   * Removes entity with the provided handle.
   *
   * @param handle Entity handle.
   * @return Handle of the removed entity.
   */
  def remove(handle: String): Future[String] = collection.remove(Json.obj("handle" -> handle)).map(_ => handle)

  /**
   * Gets entity by ID. Throws an exception if such entity does not exist.
   *
   * @param id Entity identifier.
   * @return Entity for the identifier.
   */
  def get(id: Id): Future[T] = get(Json.obj("_id" -> id))

  /**
   * Gets entity by handle. Assumes that the entity has a handle. Throws an exception if such entity does not exist.
   *
   * @param handle Handle value.
   * @return Entity for the handle.
   */
  def getByHandle(handle: String): Future[T] = get(Json.obj("handle" -> handle))

  /**
   * Finds entity by handle. Assumes that the entity has a handle.
   *
   * @param handle Handle value.
   * @return Entity for the handle.
   */
  def findByHandle(handle: String): Future[Option[T]] = find(Json.obj("handle" -> handle)).map(_.headOption)

  /**
   * Returns all entities, unsorted.
   *
   * @return All entities.
   */
  def all(): Future[Traversable[T]] = find(Json.obj())

  protected def find(selector: JsValue, sort: JsValue = JsNull, first: Boolean = false): Future[Seq[T]] = {
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


  protected def get(selector: JsValue): Future[T] = find(selector).map {
    _.headOption match {
      case Some(result) => result
      case None => throw new HrstkaException(s"No ${coll.name} for $selector!")
    }
  }

  protected def collection = db.collection[JSONCollection](coll.name)
  protected def db = reactiveMongoApi.db
}