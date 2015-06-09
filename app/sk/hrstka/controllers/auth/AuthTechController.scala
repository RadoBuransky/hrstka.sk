package sk.hrstka.controllers.auth

import com.google.inject.ImplementedBy
import play.api.mvc.{Action, AnyContent}
import sk.hrstka.controllers.auth.impl.AuthTechControllerImpl

case class AddTechForm(categoryHandle: String,
                       name: String,
                       website: String)

/**
 * Tech controller.
 */
@ImplementedBy(classOf[AuthTechControllerImpl])
trait AuthTechController {
  /**
   * Gets HTML view with all technologies.
   *
   * @return HTML view.
   */
  def all: Action[AnyContent]

  /**
   * Handles submitted form with new technology.
   *
   * @return Redirect to all technologies.
   */
  def add: Action[AnyContent]

  /**
   * Removes a technology.
   *
   * @param handle Technology handle.
   * @return Redirect to all technologies.
   */
  def remove(handle: String): Action[AnyContent]

  /**
   * Handles vote up.
   *
   * @param handle Technology handle.
   * @return Redirect to all technologies.
   */
  def voteUp(handle: String): Action[AnyContent]

  /**
   * Handles vote down.
   *
   * @param handle Technology handle.
   * @return Redirect to all technologies.
   */
  def voteDown(handle: String): Action[AnyContent]
}

