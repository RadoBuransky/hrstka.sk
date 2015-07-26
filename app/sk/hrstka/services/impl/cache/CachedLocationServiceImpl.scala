package sk.hrstka.services.impl.cache

import com.google.inject.{Inject, Singleton}
import sk.hrstka.common.HrstkaCache
import sk.hrstka.models.domain.{City, Country, Handle, Iso3166}
import sk.hrstka.repositories.{CityRepository, CompRepository}
import sk.hrstka.services.LocationService
import sk.hrstka.services.impl.NotCachedLocationService

import scala.concurrent.Future


@Singleton
final class CachedLocationServiceImpl @Inject() (hrstkaCache: HrstkaCache,
                                                 cityRepository: CityRepository,
                                                 compRepository: CompRepository,
                                                 underlying: NotCachedLocationService)
  extends LocationService {
  override def upsert(city: City): Future[Handle] = underlying.upsert(city)
  override def remove(handle: Handle): Future[Handle] = underlying.remove(handle)
  override def countries(): Future[Seq[Country]] = hrstkaCache.cacheSuccess("CachedLocationServiceImpl.countries", underlying.countries())
  override def getCountryByCode(code: Iso3166): Future[Country] = underlying.getCountryByCode(code)
  override def usedCities() = hrstkaCache.cacheSuccess("CachedLocationServiceImpl.cities", underlying.usedCities())
  override def city(handle: Handle) = underlying.city(handle)
  override def allCities() = underlying.allCities()
}
