package models.domain

import models.db

case class City(handle: String,
                sk: String) extends Identifiable {
  def id = handle
}

object City {
  def apply(city: db.City): City = City(
    handle  = city.handle,
    sk      = city.sk
  )
}

case class CityInfo(city: City, compCount: Int)
