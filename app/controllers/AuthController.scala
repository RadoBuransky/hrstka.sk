package controllers

import auth.AuthConfigImpl
import common.SupportedLang
import jp.t2v.lab.play2.auth.{AuthConfig, LoginLogout}
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, AnyContent, Controller}
import services.AuthService
import views.html

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait AuthController extends Controller with LoginLogout with AuthConfig {
  def login: Action[AnyContent]
  def logout: Action[AnyContent]
  def authenticate: Action[AnyContent]
}

object AuthController {
  def apply(authService: AuthService): AuthController = new AuthControllerImpl(authService)
}

private class AuthControllerImpl(authService: AuthService) extends AuthConfigImpl(authService) with AuthController {

  /** Your application's login form.  Alter it to fit your application */
  val loginForm = Form {
    mapping("email" -> email, "password" -> text)(authService.authenticate)(_.map(u => (u.email, "")))
      .verifying("Nesprávne meno alebo heslo", result => result.isDefined)
  }

  /** Alter the login page action to suit your application. */
  def login = Action { implicit request =>
    Ok(html.login(SupportedLang.defaultLang, loginForm))
  }

  /**
   * Return the `gotoLogoutSucceeded` method's result in the logout action.
   *
   * Since the `gotoLogoutSucceeded` returns `Future[Result]`,
   * you can add a procedure like the following.
   *
   *   gotoLogoutSucceeded.map(_.flashing(
   *     "success" -> "You've been logged out"
   *   ))
   */
  def logout = Action.async { implicit request =>
    // do something...
    gotoLogoutSucceeded
  }

  /**
   * Return the `gotoLoginSucceeded` method's result in the login action.
   *
   * Since the `gotoLoginSucceeded` returns `Future[Result]`,
   * you can add a procedure like the `gotoLogoutSucceeded`.
   */
  def authenticate = Action.async { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.debug(formWithErrors.errors.mkString(","))
        Future.successful(BadRequest(html.login(SupportedLang.defaultLang, formWithErrors)))
      },
      user => gotoLoginSucceeded(user.get.email)
    )
  }
}