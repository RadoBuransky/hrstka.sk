package sk.hrstka.models.db

import sk.hrstka.models.domain.{Country, HandleFactory, Slovakia}

object CitySpec {
  val bratislava = createCity("Bratislava", Slovakia)
  val kosice = createCity("Košice", Slovakia)
  val noveZamky = createCity("Nové Zámky", Slovakia)
  val all = Seq(bratislava, kosice, noveZamky)

  private def createCity(en: String, country: Country) = City(
    _id         = Identifiable.empty,
    handle      = HandleFactory.fromHumanName(en).value,
    name          = en,
    countryCode = country.code.value
  )
}
