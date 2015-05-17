package controllers

import auth.AuthConfigImpl
import jp.t2v.lab.play2.auth.{AuthConfig, AuthElement, LoginLogout}
import models.domain.Admin
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
  def register: Action[AnyContent]
  def registerView: Action[AnyContent]
}

object AuthController {
  def apply(authService: AuthService): AuthController = new AuthControllerImpl(authService)
}

case class LoginForm(email: String, password: String)
case class RegisterForm(email: String, password: String, passwordAgain: String)

private class AuthControllerImpl(authService: AuthService) extends AuthConfigImpl(authService) with AuthElement with AuthController {
  val loginForm = Form(mapping("email" -> email, "password" -> text)(LoginForm.apply)(LoginForm.unapply))
  val registerForm = Form(mapping(
    "email" -> email,
    "password" -> text,
    "passwordAgain" -> text)(RegisterForm.apply)(RegisterForm.unapply))

  def login = Action { implicit request =>
    Ok(html.login(loginForm))
  }

  def logout = Action.async { implicit request =>
    gotoLogoutSucceeded
  }

  def authenticate = Action.async { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(html.login(formWithErrors))),
      loginForm => {
        authService.authenticate(loginForm.email, loginForm.password).flatMap {
          case Some(user) => gotoLoginSucceeded(user.email)
          case _ => Future.successful(Unauthorized)
        }
      }
    )
  }

  override def register: Action[AnyContent] = AsyncStack(AuthorityKey -> Admin) { implicit request =>
    val form = registerForm.bindFromRequest.get
    form.password match {
      case form.passwordAgain =>
        authService.createUser(form.email, form.password).map { user =>
          Redirect(AppLoader.routes.compController.all())
        }
      case _ => Future.successful(BadRequest("Passwords do not match!"))
    }
  }

  override def registerView: Action[AnyContent] = StackAction(AuthorityKey -> Admin) { implicit request =>
    Ok(html.register())
  }
}