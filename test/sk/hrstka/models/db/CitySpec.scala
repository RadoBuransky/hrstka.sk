package sk.hrstka.models.db

import sk.hrstka.models.domain.HandleFactory

object CitySpec {
  val bratislava = createCity("Bratislava")
  val kosice = createCity("Košice")
  val noveZamky = createCity("Nové Zámky")
  val all = Seq(bratislava, kosice, noveZamky)

  private def createCity(sk: String) = City(
    _id     = Identifiable.empty,
    handle  = HandleFactory.fromHumanName(sk).value,
    en      = sk
  )
}
