package sk.hrstka.controllers

import com.google.inject._
import play.api.mvc._
import sk.hrstka.controllers.impl.AppControllerImpl

/**
 * Application controller.
 */
@ImplementedBy(classOf[AppControllerImpl])
trait AppController {
  /**
   * Redirects to URL without trailing /
   *
   * @param path Path to fix.
   * @return 301 MovedPermanently HTTP result.
   */
  def untrail(path: String): Action[AnyContent]
}

