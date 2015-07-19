package sk.hrstka.models.ui

import sk.hrstka.models

case class City(handle: String,
                sk: String)

object CityFactory {
  def apply(city: models.domain.City): City = City(
    handle  = city.handle.value,
    sk      = city.en
  )
}
