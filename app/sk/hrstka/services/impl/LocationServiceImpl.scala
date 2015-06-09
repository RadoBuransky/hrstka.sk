package sk.hrstka.services.impl

import com.google.inject.{Inject, Singleton}
import sk.hrstka.models.db.{City, Identifiable}
import sk.hrstka.models.domain
import sk.hrstka.models.domain.{CityFactory, Handle, HandleFactory}
import sk.hrstka.repositories.{CompRepository, CityRepository}
import sk.hrstka.services.LocationService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
final class LocationServiceImpl @Inject() (cityRepository: CityRepository,
                                           compRepository: CompRepository) extends LocationService {
  override def all(): Future[Seq[domain.City]] = {
    cityRepository.all().flatMap { dbCities =>
      // Map to domain model
      val cities = dbCities.map(CityFactory.apply)

      // Get all companies
      compRepository.all(None, None).map { dbComps =>
        // Oder by number of companies
        cities.toSeq.sortBy(city => -1 * dbComps.count(_.city == city.handle.value))
      }
    }
  }

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
