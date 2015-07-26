package sk.hrstka.models.domain

import sk.hrstka.models

/**
 * City.
 *
 * @param handle Human-friendly identifier.
 * @param en English name of the city.
 * @param country Country where the city is located.
 */
case class City(handle: Handle,
                en: String,
                country: Country)

object CityFactory {
  def apply(city: models.db.City, country: Country): City = City(
    handle  = Handle(city.handle),
    en      = city.en,
    country = country
  )
}

case class CityInfo(city: City, compCount: Int)
