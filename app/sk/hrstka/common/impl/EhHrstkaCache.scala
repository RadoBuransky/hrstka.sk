package sk.hrstka.common.impl

import com.google.inject.{Inject, Singleton}
import net.sf.ehcache.{CacheManager, Ehcache}
import play.api.cache.{CacheApi, EhCacheApi}
import play.api.inject.ApplicationLifecycle
import sk.hrstka.common.{Logging, HrstkaCache}

import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import scala.reflect.ClassTag

@Singleton
final class EhHrstkaCache @Inject() (applicationLifecycle: ApplicationLifecycle)
  extends HrstkaCache with CacheApi with Logging {
  import EhHrstkaCache._

  override def set(key: String, value: Any, expiration: Duration): Unit = playEhCacheApi.set(key, value, expiration)
  override def get[T: ClassTag](key: String): Option[T] = playEhCacheApi.get[T](key)
  override def getOrElse[A: ClassTag](key: String, expiration: Duration)(orElse: => A): A = playEhCacheApi.getOrElse[A](key, expiration)(orElse)
  override def remove(key: String): Unit = playEhCacheApi.remove(key)

  override def cacheSuccess[T: ClassTag](key: String)(value: => Future[T]): Future[T] = {
    get[T](key) match {
      case Some(result) =>
        logger.info(s"Getting from cache. [$key]")
        Future.successful(result)
      case None =>
        value.onSuccess {
          case result =>
            logger.info(s"Putting to cache. [$key]")
            set(key, result)
        }
        value
    }
  }

  override def invalidate(): Unit = {
    cache.removeAll()
    logger.debug("Cache invalidated.")
  }

  private lazy val cacheManager = {
    val result = CacheManager.create()
    logger.info("CacheManager created.")

    applicationLifecycle.addStopHook { () =>
      Future.successful {
        result.shutdown()
        logger.info("CacheManager shut down.")
      }
    }

    result
  }
  private lazy val playEhCacheApi = new EhCacheApi(cache)
  private lazy val cache: Ehcache = {
    logger.info("Ehcache created")
    cacheManager.addCache(name)
    cacheManager.getEhcache(name)
  }
}

private object EhHrstkaCache {
  val name = "GlobalHrstkaCache"
}