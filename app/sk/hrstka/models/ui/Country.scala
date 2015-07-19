package sk.hrstka.models.ui

import sk.hrstka.models.domain

/**
 * Country.
 *
 * @param code Country code.
 * @param en English name of the country.
 */
case class Country(code: String,
                   en: String)

object CountryFactory {
  def apply(country: domain.Country): Country =
    Country(
      code  = country.code.value,
      en    = country.en
    )
}
