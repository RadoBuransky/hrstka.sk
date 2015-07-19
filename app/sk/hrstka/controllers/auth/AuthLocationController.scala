package sk.hrstka.controllers.auth

import com.google.inject.ImplementedBy
import play.api.mvc.{Action, AnyContent}
import sk.hrstka.controllers.auth.impl.AuthLocationControllerImpl

case class AddCityForm(countryCode: String,
                       city: String)

/**
 * Location controller.
 */
@ImplementedBy(classOf[AuthLocationControllerImpl])
trait AuthLocationController {
  /**
   * Gets HTML view with all locations (countries and cities).
   *
   * @return HTML view.
   */
  def all: Action[AnyContent]

  /**
   * Handles submitted form with new city.
   *
   * @return Redirect to all locations.
   */
  def add: Action[AnyContent]

  /**
   * Removes a city.
   *
   * @param handle City handle.
   * @return Redirect to all locations.
   */
  def remove(handle: String): Action[AnyContent]
}
