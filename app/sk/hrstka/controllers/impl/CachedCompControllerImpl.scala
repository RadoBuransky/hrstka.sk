package sk.hrstka.controllers.impl

import com.google.inject.{Inject, Singleton}
import play.api.Application
import play.api.cache.Cached
import play.api.i18n.MessagesApi
import sk.hrstka.controllers.CompController
import sk.hrstka.services._

@Singleton
final class CachedCompControllerImpl @Inject() (compService: CompService,
                                                markdownService: MarkdownService,
                                                authService: AuthService,
                                                techService: TechService,
                                                locationService: LocationService,
                                                application: Application,
                                                messagesApi: MessagesApi,
                                                protected val cached: Cached)
  extends CompController with HrstkaCachedController {
  private val underlying = new CompControllerImpl(
    compService,
    markdownService,
    authService,
    techService,
    locationService,
    application,
    messagesApi
  )

  override def get(businessNumber: String) = underlying.get(businessNumber)
  override def women = cacheOkStatus { underlying.women }
  override def all = cacheOkStatus { underlying.all }
  override def cityTech(cityHandle: String, techHandle: String) = cacheOkStatus { underlying.cityTech(cityHandle, techHandle) }
}
