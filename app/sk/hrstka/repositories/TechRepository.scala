package sk.hrstka.repositories

import com.google.inject.ImplementedBy
import sk.hrstka.models.db.Identifiable.Id
import sk.hrstka.models.db.{Identifiable, Tech}
import sk.hrstka.repositories.mongoDb.MongoTechRepository

import scala.concurrent.Future

@ImplementedBy(classOf[MongoTechRepository])
trait TechRepository {
  def upsert(tech: Tech): Future[Id]
  def all(): Future[Seq[Tech]]
  def getByHandle(handle: String): Future[Tech]
}
