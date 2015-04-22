package services

import java.net.URL

import models.domain.Comp
import models.domain.Identifiable._

import scala.concurrent.Future

trait CompService {
  def get(compId: Id): Future[Comp]
  def insert(name: String, website: URL, location: String, codersCount: Option[Int], femaleCodersCount: Option[Int],
             note: String, userId: Id, techNames: Seq[String]): Future[Id]
  def update(comp: Comp, techNames: Seq[String], userId: Id): Future[Unit]
  def all(): Future[Seq[Comp]]
}
