package sk.hrstka.common

import com.google.inject.AbstractModule
import play.api.cache.CacheApi
import sk.hrstka.common.impl.{DefaultEhHrstkaCache, HrstkaEhHrstkaCache}

/**
 * Main dependency injection module for the application.
 */
final class HrstkaModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[ApplicationLifecycle])
      .to(classOf[ApplicationLifecycleImpl])
      .asEagerSingleton()

    bind(classOf[CacheApi])
      .to(classOf[DefaultEhHrstkaCache])

    bind(classOf[HrstkaCache])
      .to(classOf[HrstkaEhHrstkaCache])
  }
}
