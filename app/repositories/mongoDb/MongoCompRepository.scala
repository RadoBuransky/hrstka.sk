package repositories.mongoDb

import models.db.Identifiable._
import models.db.JsonFormats._
import models.db.{Comp, Identifiable}
import play.api.Logger
import play.api.libs.json.Json
import reactivemongo.bson.BSONObjectID
import repositories.CompRepository

import scala.concurrent.Future

class MongoCompRepository extends BaseMongoRepository(CompCollection) with CompRepository {
  override def get(compId: Id): Future[Comp] = get[Comp](compId)
  override def all(city: Option[Handle] = None, tech: Option[Handle] = None): Future[Seq[Comp]] = {

    val cityQuery = city match {
      case Some(cityHandle) => Json.obj("city" -> cityHandle)
      case None => Json.obj()
    }

    val techQuery = tech match {
      case Some(techHandle) => Json.obj("techs" -> Json.obj(MongoOperators.in -> Json.arr(techHandle)))
      case None => Json.obj()
    }

    val q = cityQuery ++ techQuery
    Logger.debug(s"$cityQuery, $techQuery")

    find[Comp](q)
  }
  override def upsert(comp: Comp): Future[Id] = {
    val compToUpsert = if (comp._id == Identifiable.empty) comp.copy(_id = BSONObjectID.generate) else comp
    super[BaseMongoRepository].upsert(compToUpsert)
  }
}
