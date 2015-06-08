package sk.hrstka.common

import com.google.inject.AbstractModule

/**
 * Main dependency injection module for the application.
 */
final class HrstkaModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[ApplicationLifecycle])
      .to(classOf[ApplicationLifecycleImpl])
      .asEagerSingleton()
  }
}
