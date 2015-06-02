package repositories

import com.google.inject.ImplementedBy
import models.db.Comp
import models.db.Identifiable._
import repositories.mongoDb.MongoCompRepository

import scala.concurrent.Future

@ImplementedBy(classOf[MongoCompRepository])
trait CompRepository {
  def get(id: Id): Future[Comp]
  def upsert(comp: Comp): Future[Id]
  def all(city: Option[Handle] = None, tech: Option[Handle] = None): Future[Seq[Comp]]
}
