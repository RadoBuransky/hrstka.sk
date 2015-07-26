package sk.hrstka.models.domain

import sk.hrstka.models

object CitySpec {
  val bratislava = CityFactory(models.db.CitySpec.bratislava, Slovakia)
  val kosice = CityFactory(models.db.CitySpec.kosice, Slovakia)
  val noveZamky = CityFactory(models.db.CitySpec.noveZamky, Slovakia)
  lazy val all = Seq(bratislava, kosice, noveZamky)
  def forHandle(handle: String): City = all.find(_.handle.value == handle).get
}