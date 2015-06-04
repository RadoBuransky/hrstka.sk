package models.ui

import models.domain

case class City(handle: String,
                sk: String)

object CityFactory {
  def apply(city: domain.City): City = City(
    handle  = city.handle.value,
    sk      = city.sk
  )
}
