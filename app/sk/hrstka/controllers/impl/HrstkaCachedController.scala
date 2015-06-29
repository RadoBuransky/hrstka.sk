package sk.hrstka.controllers.impl

import play.api.cache.Cached
import play.api.mvc.{Controller, EssentialAction, RequestHeader}

import scala.concurrent.duration._

/**
 * Hrstka-specific controller caching.
 */
private[impl] trait HrstkaCachedController {
  self: Controller =>

  import HrstkaCachedController._

  protected def cached: Cached

  protected def cacheOkStatus(action: EssentialAction): EssentialAction = cacheOkStatus(_.uri)(action)
  protected def cacheOkStatus(key: String)(action: EssentialAction): EssentialAction = cacheOkStatus(_ => key)(action)
  protected def cacheOkStatus(key: RequestHeader => String)(action: EssentialAction): EssentialAction =
    cached.status(key, OK, oneDayDuration) { action }
}

private object HrstkaCachedController {
  val oneDayDuration = 24.hours.toSeconds.toInt
}
