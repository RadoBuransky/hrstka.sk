package models.domain

import models.db

object CitySpec {
  val bratislava = CityFactory(db.CitySpec.bratislava)
  val kosice = CityFactory(db.CitySpec.kosice)
  val noveZamky = CityFactory(db.CitySpec.noveZamky)
  lazy val allCities = Set(bratislava, kosice, noveZamky)
  def forHandle(handle: String): City = allCities.find(_.handle.value == handle).get
}