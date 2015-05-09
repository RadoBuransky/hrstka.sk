package repositories

import models.db.City

import scala.concurrent.Future

trait CityRepository {
  def get(handle: String): Future[City]
  def insert(city: City): Future[String]
}
