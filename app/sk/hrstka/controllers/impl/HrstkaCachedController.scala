package sk.hrstka.controllers.impl

import play.api.cache.Cached
import play.api.http.Status
import play.api.mvc.{EssentialAction, RequestHeader}

import scala.concurrent.duration._

/**
 * Hrstka-specific controller caching.
 */
private[impl] trait HrstkaCachedController {
  import HrstkaCachedController._

  protected def cached: Cached

  protected def cacheOkStatus(action: EssentialAction): EssentialAction = cacheOkStatus(_.uri)(action)
  protected def cacheOkStatus(key: String)(action: EssentialAction): EssentialAction = cacheOkStatus(_ => key)(action)
  protected def cacheOkStatus(key: RequestHeader => String)(action: EssentialAction): EssentialAction = {
    cached.status(key, Status.OK, oneDayDuration) {
      action
    }
  }
}

private object HrstkaCachedController {
  val oneDayDuration = 24.hours.toSeconds.toInt
}
