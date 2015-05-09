package models.domain

import models.db

case class City(id: Identifiable.Id,
                handle: String,
                sk: String) extends Identifiable

object City {
  def apply(city: db.City): City = City(
    id      = city._id.stringify,
    handle  = city.handle,
    sk      = city.sk
  )
}

case class CityInfo(city: City, compCount: Int)
