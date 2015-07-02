package sk.hrstka.common

import play.api.cache.CacheApi

import scala.concurrent.Future
import scala.reflect.ClassTag

/**
 * Shared Hrstka cache interface.
 */
trait HrstkaCache extends CacheApi {
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
