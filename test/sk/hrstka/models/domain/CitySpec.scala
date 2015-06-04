package sk.hrstka.models.domain

import sk.hrstka.models

object CitySpec {
  val bratislava = CityFactory(models.db.CitySpec.bratislava)
  val kosice = CityFactory(models.db.CitySpec.kosice)
  val noveZamky = CityFactory(models.db.CitySpec.noveZamky)
  lazy val allCities = Set(bratislava, kosice, noveZamky)
  def forHandle(handle: String): City = allCities.find(_.handle.value == handle).get
}