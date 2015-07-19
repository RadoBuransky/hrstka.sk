package sk.hrstka.services.impl.cache

import org.mockito.Mockito._
import sk.hrstka.common.HrstkaCache

import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.reflect.ClassTag

private[cache] abstract class CacheTestScope[S <: AnyRef] {
  def service: S
  def underlying: S
  val hrstkaCache = new TestHrstkaCache

  protected def verifyNoCaching(action: (S) => Any): Unit = verifyCaching(action, 0)
  protected def verifyCaching[T](action: (S) => T): Unit = verifyCaching(action, 1)

  private def verifyCaching(action: (S) => Any, times: Int): Unit = {
    action(service)
    action(verify(underlying))
    assert(hrstkaCache.cacheSuccess == times)
    verifyNoMoreInteractions(underlying)
  }

  protected class TestHrstkaCache extends HrstkaCache {
    var cacheSuccess = 0

    override def cacheSuccess[T: ClassTag](key: String, value: => Future[T]): Future[T] = {
      cacheSuccess += 1
      value
    }
    override def invalidateOnSuccess[T](action: => Future[T]): Future[T] = action
    override def invalidate(): Unit = {}
    override def set(key: String, value: Any, expiration: Duration): Unit = {}
    override def get[T: ClassTag](key: String): Option[T] = None
    override def getOrElse[A: ClassTag](key: String, expiration: Duration)(orElse: => A): A = orElse
    override def remove(key: String): Unit = {}
  }
}
