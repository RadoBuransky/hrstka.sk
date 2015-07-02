package sk.hrstka.services.impl.cache

import com.google.inject.{Inject, Singleton}
import sk.hrstka.common.HrstkaCache
import sk.hrstka.models.domain.Handle
import sk.hrstka.repositories.{CityRepository, CompRepository}
import sk.hrstka.services.LocationService
import sk.hrstka.services.impl.LocationServiceImpl

@Singleton
final class CachedLocationServiceImpl @Inject() (hrstkaCache: HrstkaCache,
                                                 cityRepository: CityRepository,
                                                 compRepository: CompRepository)
  extends LocationService {

  private val underlying = new LocationServiceImpl(cityRepository, compRepository)

  override def all() = hrstkaCache.cacheSuccess("CachedLocationServiceImpl.all") { underlying.all() }
  override def getOrCreateCity(sk: String) = underlying.getOrCreateCity(sk)
  override def get(handle: Handle) = underlying.get(handle)
}
