package sk.hrstka.models.domain

import sk.hrstka.models

case class City(handle: Handle,
                sk: String)

object CityFactory {
  def apply(city: models.db.City): City = City(
    handle  = Handle(city.handle),
    sk      = city.sk
  )
}

case class CityInfo(city: City, compCount: Int)
