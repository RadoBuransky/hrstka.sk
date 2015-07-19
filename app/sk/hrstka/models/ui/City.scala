package sk.hrstka.models.ui

import sk.hrstka.models

/**
 * City.
 *
 * @param handle Human-friendly identifier.
 * @param en English name of the city.
 * @param country Country where the city is located.
 */
case class City(handle: String,
                en: String,
                country: Country)

object CityFactory {
  def apply(city: models.domain.City): City = City(
    handle  = city.handle.value,
    en      = city.en,
    country = CountryFactory(city.country)
  )
}
