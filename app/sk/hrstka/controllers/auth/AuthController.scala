package sk.hrstka.controllers.auth

import com.google.inject.ImplementedBy
import play.api.mvc.{Action, AnyContent, Controller}
import sk.hrstka.controllers.auth.impl.AuthControllerImpl

case class LoginForm(email: String, password: String)
case class RegisterForm(email: String, password: String, passwordAgain: String)

/**
 * Authentication controller.
 */
@ImplementedBy(classOf[AuthControllerImpl])
trait AuthController extends Controller {
  /**
   * Gets HTML view with the login form.
   *
   * @return HTML view.
   */
  def login: Action[AnyContent]

  /**
   * Logs out the current user and redirects somewhere.
   *
   * @return Redirect.
   */
  def logout: Action[AnyContent]

  /**
   * Handles submitted login form.
   *
   * @return Redirect.
   */
  def authenticate: Action[AnyContent]

  /**
   * Gets HTML view with a form to register a new user.
   *
   * @return HTML view.
   */
  def registerView: Action[AnyContent]

  /**
   * Handles submitten registration form.
   *
   * @return Redirect.
   */
  def register: Action[AnyContent]
}
