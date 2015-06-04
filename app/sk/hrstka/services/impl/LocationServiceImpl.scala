package sk.hrstka.services.impl

import com.google.inject.{Inject, Singleton}
import sk.hrstka.models.db.{City, Identifiable}
import sk.hrstka.models.domain
import sk.hrstka.models.domain.{CityFactory, Handle, HandleFactory}
import sk.hrstka.repositories.CityRepository
import sk.hrstka.services.LocationService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
final class LocationServiceImpl @Inject() (cityRepository: CityRepository) extends LocationService {
  override def all(): Future[Seq[domain.City]] = cityRepository.all().map(_.map(CityFactory.apply))

  override def get(handle: Handle): Future[domain.City] = cityRepository.getByHandle(handle.value).map(CityFactory.apply)

  override def getOrCreateCity(humanName: String): Future[domain.City] = {
    val handle = HandleFactory.fromHumanName(humanName)
    cityRepository.findByHandle(handle.value).map {
      case Some(city) => CityFactory(city)
      case None =>
        val newCity = City(
        _id     = Identifiable.empty,
        handle  = handle.value,
        sk      = humanName)

        cityRepository.insert(newCity)
        CityFactory(newCity)
    }
  }
}
