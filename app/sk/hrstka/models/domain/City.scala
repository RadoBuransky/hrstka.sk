package sk.hrstka.models.domain

import sk.hrstka.models

/**
 * City.
 *
 * @param handle Human-friendly identifier.
 * @param en English name of the city.
 */
case class City(handle: Handle,
                en: String,
                country: Country)

object CityFactory {
  def apply(city: models.db.City): City = City(
    handle  = Handle(city.handle),
    en      = city.sk,
    // TODO: ...
    country = Slovakia
  )
}

case class CityInfo(city: City, compCount: Int)
