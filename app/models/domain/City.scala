package models.domain

class City(val slovakName: String) extends Identifiable {
  def id = Identifiable.fromHumanName(slovakName)
}

case class CityInfo(city: City, compCount: Int)
