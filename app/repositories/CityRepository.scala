package repositories

import models.db.City

import scala.concurrent.Future

trait CityRepository {
  def get(handle: String): Future[City]
  def find(handle: String): Future[Option[City]]
  def insert(city: City): Future[String]
}
