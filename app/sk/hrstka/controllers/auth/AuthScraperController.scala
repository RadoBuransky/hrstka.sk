package sk.hrstka.controllers.auth

import com.google.inject.ImplementedBy
import play.api.mvc.{Action, AnyContent}
import sk.hrstka.controllers.auth.impl.AuthScraperControllerImpl

/**
 * HTML scraper controller.
 */
@ImplementedBy(classOf[AuthScraperControllerImpl])
trait AuthScraperController {
  def scrape(): Action[AnyContent]
}
