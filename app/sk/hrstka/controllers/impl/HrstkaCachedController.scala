package sk.hrstka.controllers.impl

import play.api.cache.Cached
import play.api.http.Status
import play.api.mvc.{EssentialAction, RequestHeader}
import sk.hrstka.common.{HrstkaCache, Logging}
import sk.hrstka.controllers.auth.impl.HrstkaAuthConfig

import scala.concurrent.duration._

/**
 * Hrstka-specific controller caching.
 */
private[impl] trait HrstkaCachedController extends Logging {
  import HrstkaCachedController._

  private lazy val cached = new Cached(hrstkaCache)

  protected def hrstkaCache: HrstkaCache

  protected def cacheOkStatus(action: EssentialAction): EssentialAction = cacheOkStatus(_.uri)(action)
  protected def cacheOkStatus(key: String)(action: EssentialAction): EssentialAction = cacheOkStatus(_ => key)(action)
  protected def cacheOkStatus(key: RequestHeader => String)(action: EssentialAction): EssentialAction = {
    EssentialAction { requestHeader =>
      requestHeader.cookies.get(HrstkaAuthConfig.cookieName) match {
        case Some(_) => action(requestHeader)
        case None => cached.status(key, Status.OK, oneHourDuration)(action)(requestHeader)
      }
    }
  }
}

private object HrstkaCachedController {
  val oneHourDuration = 1.hours.toSeconds.toInt
}
