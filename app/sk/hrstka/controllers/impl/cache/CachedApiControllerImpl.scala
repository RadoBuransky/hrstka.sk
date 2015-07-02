package sk.hrstka.controllers.impl.cache

import com.google.inject.{Inject, Singleton}
import play.api.cache.Cached
import sk.hrstka.controllers.ApiController
import sk.hrstka.controllers.impl.{ApiControllerImpl, HrstkaCachedController}
import sk.hrstka.services.{CompService, LocationService, TechService}

@Singleton
final class CachedApiControllerImpl @Inject() (compService: CompService,
                                               techService: TechService,
                                               locationService: LocationService,
                                               protected val cached: Cached)
  extends ApiController with HrstkaCachedController {
  private val underlying = new ApiControllerImpl(
    compService,
    techService,
    locationService)

  override def comps() = cacheOkStatus { underlying.comps() }
  override def techs() = cacheOkStatus { underlying.techs() }
  override def comp(businessNumber: String) = underlying.comp(businessNumber)
  override def tech(handle: String) = underlying.tech(handle)
  override def cities() =  cacheOkStatus { underlying.cities() }
}
