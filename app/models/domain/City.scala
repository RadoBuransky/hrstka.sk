package models.domain

import models.db

case class City(handle: Handle,
                sk: String)

object City {
  def apply(city: db.City): City = City(
    handle  = Handle(city.handle),
    sk      = city.sk
  )
}

case class CityInfo(city: City, compCount: Int)
