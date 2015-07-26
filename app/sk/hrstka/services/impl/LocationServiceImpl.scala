package sk.hrstka.services.impl

import com.google.inject.{ImplementedBy, Inject, Singleton}
import sk.hrstka.common.HrstkaException
import sk.hrstka.models.db
import sk.hrstka.models.domain._
import sk.hrstka.repositories.{CityRepository, CompRepository}
import sk.hrstka.services.LocationService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Marker trait.
 */
@ImplementedBy(classOf[LocationServiceImpl])
private[impl] trait NotCachedLocationService extends LocationService

@Singleton
final class LocationServiceImpl @Inject() (cityRepository: CityRepository,
                                           compRepository: CompRepository) extends NotCachedLocationService {
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
        case None => throw new HrstkaException(s"No country exists for the code! [${code.value}]")
      }
    }

  override def usedCities(): Future[Seq[City]] = {
    // Get all cities
    allCities().flatMap { cities =>
      // Get all companies
      compRepository.all(None, None).map { dbComps =>
        // Get number of companies for each city
        val cityCount = cities.map { city => (city, dbComps.count(_.city == city.handle.value)) }

        // Oder by number of companies
        cityCount.filter(_._2 > 0).toSeq.sortBy(-1 * _._2).map(_._1)
      }
    }
  }

  override def allCities(): Future[Traversable[City]] = {
    cityRepository.all().map { dbCities =>
      // Map to domain model
      dbCities.map(CityFactory.apply)
    }
  }

  override def city(handle: Handle): Future[City] = cityRepository.getByHandle(handle.value).map(CityFactory.apply)
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