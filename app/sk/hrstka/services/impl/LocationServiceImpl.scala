package sk.hrstka.services.impl

import com.google.inject.{Inject, Singleton}
import sk.hrstka.common.HrstkaException
import sk.hrstka.models.db
import sk.hrstka.models.domain._
import sk.hrstka.repositories.{CityRepository, CompRepository}
import sk.hrstka.services.LocationService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
final class LocationServiceImpl @Inject() (cityRepository: CityRepository,
                                           compRepository: CompRepository) extends LocationService {
  override def upsert(city: City): Future[Handle] =
    cityRepository.insert(db.City(
      _id         = db.Identifiable.empty,
      handle      = city.handle.value,
      en          = city.en,
      countryCode = city.country.code.value)).map { _ =>
      city.handle
    }

  override def remove(handle: Handle): Future[Handle] = ???

  override def countries(): Future[Seq[Country]] = Future.successful(LocationServiceImpl.countries)

  override def getCountryByCode(code: Iso3166): Future[Country] =
    countries().map { allCountries =>
      allCountries.find(_.code == code) match {
        case Some(country) => country
        case None => throw new HrstkaException(s"No country exists for the code! [$code]")
      }
    }

  override def cities(): Future[Seq[City]] = {
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

  override def city(handle: Handle): Future[City] = cityRepository.getByHandle(handle.value).map(CityFactory.apply)

  override def getOrCreateCity(humanName: String): Future[City] = {
    val handle = HandleFactory.fromHumanName(humanName)
    cityRepository.findByHandle(handle.value).map {
      case Some(city) => CityFactory(city)
      case None => ???
    }
  }
}

private object LocationServiceImpl {
  val countries = Seq(
    Slovakia,
    CzechRepublic,
    Austria,
    Hungary,
    Poland,
    Ukraine,
    Germany
  )
}