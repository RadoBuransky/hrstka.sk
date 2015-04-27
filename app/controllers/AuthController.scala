package controllers

import auth.AuthConfigImpl
import common.SupportedLang
import jp.t2v.lab.play2.auth.{AuthConfig, LoginLogout}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, AnyContent, Controller}
import services.AuthService
import views.html

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait AuthController extends Controller with LoginLogout with AuthConfig {
  def login: Action[AnyContent]
  def logout: Action[AnyContent]
  def authenticate: Action[AnyContent]
}

object AuthController {
  def apply(authService: AuthService): AuthController = new AuthControllerImpl(authService)
}

case class LoginForm(email: String, password: String)

private class AuthControllerImpl(authService: AuthService) extends AuthConfigImpl(authService) with AuthController {
  val loginForm = Form(mapping("email" -> email, "password" -> text)(LoginForm.apply)(LoginForm.unapply))

  def login = Action { implicit request =>
    Ok(html.login(SupportedLang.defaultLang, loginForm))
  }

  def logout = Action.async { implicit request =>
    gotoLogoutSucceeded
  }

  def authenticate = Action.async { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(html.login(SupportedLang.defaultLang, formWithErrors))),
      loginForm => {
        authService.authenticate(loginForm.email, loginForm.password).flatMap {
          case Some(user) => gotoLoginSucceeded(user.email)
          case _ => Future.successful(Unauthorized)
        }
      }
    )
  }
}