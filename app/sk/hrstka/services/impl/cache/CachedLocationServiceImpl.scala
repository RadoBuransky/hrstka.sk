package sk.hrstka.services.impl.cache

import com.google.inject.{Inject, Singleton}
import sk.hrstka.common.HrstkaCache
import sk.hrstka.models.domain.{Country, Handle}
import sk.hrstka.repositories.{CityRepository, CompRepository}
import sk.hrstka.services.LocationService
import sk.hrstka.services.impl.LocationServiceImpl

import scala.concurrent.Future

@Singleton
final class CachedLocationServiceImpl @Inject() (hrstkaCache: HrstkaCache,
                                                 cityRepository: CityRepository,
                                                 compRepository: CompRepository)
  extends LocationService {

  private val underlying = new LocationServiceImpl(cityRepository, compRepository)

  override def countries(): Future[Seq[Country]] = hrstkaCache.cacheSuccess("CachedLocationServiceImpl.countries") { underlying.countries() }
  override def cities() = hrstkaCache.cacheSuccess("CachedLocationServiceImpl.cities") { underlying.cities() }
  override def getOrCreateCity(sk: String) = underlying.getOrCreateCity(sk)
  override def city(handle: Handle) = underlying.city(handle)
}
