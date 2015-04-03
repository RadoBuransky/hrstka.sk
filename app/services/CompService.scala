package services

import java.net.URL

import models.domain.Comp
import models.domain.Identifiable._

import scala.concurrent.Future

trait CompService {
  def get(compId: Id): Future[Comp]
  def insert(name: String, website: URL, location: String, codersCount: Option[Int], femaleCodersCount: Option[Int],
             note: String, userId: Id): Future[Id]
  def update(comp: Comp): Future[Unit]
  def all(): Future[Seq[Comp]]
  def addTech(techName: String, compId: Id, userId: Id): Future[Id]
  def removeTech(compId: Id, techId: Id, userId: Id): Future[Unit]
}
