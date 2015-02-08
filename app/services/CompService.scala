package services

import java.net.URL

import models.domain.Comp
import models.domain.Identifiable._

import scala.concurrent.Future

trait CompService {
  def insert(name: String, website: URL, userId: Id): Future[Id]
  def all(): Future[Seq[Comp]]
  def addTech(compId: Id, techId: Id, userId: Id): Future[Id]
}
