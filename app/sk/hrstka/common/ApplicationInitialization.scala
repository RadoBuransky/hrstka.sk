package sk.hrstka.common

import com.google.inject.{Inject, Singleton}
import sk.hrstka.repositories.scripts.DbManager

import scala.concurrent.Await
import scala.concurrent.duration._

/**
 * Application lifecycle handler.
 */
sealed trait ApplicationLifecycle {
  /**
   * Executed after application has been started.
   */
  def onStart()
}

@Singleton
final class ApplicationLifecycleImpl @Inject() (dbManager: DbManager) extends ApplicationLifecycle {
  // Invoke on start from the constructor (as described in Play documentation)
  onStart()

  override def onStart(): Unit = {
    // Initialize databaze
    Await.result(dbManager.applicationInit(), 3.minutes)
  }
}