package repositories

import models.db.Identifiable._

import scala.concurrent.Future

trait CompTechRepository {
  def add(authorId: Id, compId: Id, techId: Id): Future[Id]
  def del(compTechId: Id, authorId: Id)
  def getTechs(compId: Id): Future[Seq[Id]]
  def getUnassignedTechs(compId: Id): Future[Seq[Id]]
}
