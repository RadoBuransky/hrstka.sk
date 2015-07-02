package sk.hrstka.common

import com.google.inject.ImplementedBy
import play.api.mvc.EssentialAction
import sk.hrstka.common.impl.EhHrstkaCache

import scala.concurrent.Future
import scala.reflect.ClassTag

/**
 * Shared Hrstka cache interface.
 */
trait HrstkaCache {
  /**
   * Caches result produced by the Future is completes with success.
   *
   * @param key Cache key.
   * @param value Function to return Future.
   * @tparam T Type of value to cache.
   * @return
   */
  def cacheSuccess[T: ClassTag](key: String)(value: => Future[T]): Future[T]

  /**
   * Invalidates everything if the action completes with success.
   *
   * @param action Action to trigger invalidation.
   * @tparam T Action result type.
   * @return Action itself.
   */
  def invalidateOnSuccess[T](action: => Future[T]): Future[T]

  /**
   * Invalidates everything.
   */
  def invalidate(): Unit
}
