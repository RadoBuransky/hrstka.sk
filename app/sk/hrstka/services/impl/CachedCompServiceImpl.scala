package sk.hrstka.services.impl

import com.google.inject.{Inject, Singleton}
import sk.hrstka.common.HrstkaCache
import sk.hrstka.models.domain._
import sk.hrstka.repositories.{CompRepository, CompVoteRepository}
import sk.hrstka.services.{CompService, LocationService, TechService}

@Singleton
final class CachedCompServiceImpl @Inject() (compRepository: CompRepository,
                                             compVoteRepository: CompVoteRepository,
                                             techService: TechService,
                                             locationService: LocationService,
                                             hrstkaCache: HrstkaCache) extends CompService {
  private val underlying = new CompServiceImpl(
    compRepository,
    compVoteRepository,
    techService,
    locationService
  )

  override def upsert(comp: Comp, techHandles: Set[Handle], userId: Id) = underlying.upsert(comp, techHandles, userId)
  override def voteUp(businessNumber: BusinessNumber, userId: Id) = underlying.voteUp(businessNumber, userId)
  override def get(businessNumber: BusinessNumber) = underlying.get(businessNumber)
  override def voteDown(businessNumber: BusinessNumber, userId: Id) = underlying.voteDown(businessNumber, userId)
  override def all(city: Option[Handle], tech: Option[Handle]) =
    hrstkaCache.cacheSuccess("CompServiceImpl.all(" + city.toString + "," + tech.toString + ")") {
      underlying.all(city, tech)
    }
  override def topWomen() = underlying.topWomen()
  override def voteFor(businessNumber: BusinessNumber, userId: Id) = underlying.voteFor(businessNumber, userId)
}
