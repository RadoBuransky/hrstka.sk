package repositories

import models.db.Identifiable._
import models.db.Tech

import scala.concurrent.Future

trait CompTechRepository {
  def add(authorId: Id, compId: Id, techId: Id): Future[Id]
  def del(compTechId: Id, authorId: Id)
  def getTechs(compId: Id): Future[Seq[Tech]]
  def getUnassignedTechs(compId: Id): Future[Seq[Tech]]
}
