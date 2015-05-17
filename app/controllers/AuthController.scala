package controllers

import auth.AuthConfigImpl
import jp.t2v.lab.play2.auth.{AuthConfig, AuthElement, LoginLogout}
import models.domain.Admin
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, AnyContent, Controller}
import services.{AuthService, LocationService, TechService}
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

case class LoginForm(email: String, password: String)
case class RegisterForm(email: String, password: String, passwordAgain: String)

class AuthControllerImpl(authService: AuthService,
                         protected val locationService: LocationService,
                         protected val techService: TechService) extends AuthConfigImpl(authService) with AuthElement with AuthController with MainModelProvider {
  val loginForm = Form(mapping("email" -> email, "password" -> text)(LoginForm.apply)(LoginForm.unapply))
  val registerForm = Form(mapping(
    "email" -> email,
    "password" -> text,
    "passwordAgain" -> text)(RegisterForm.apply)(RegisterForm.unapply))

  def login = Action.async { implicit request =>
    withMainModel { implicit mainModel =>
      Ok(html.login(loginForm))
    }
  }

  def logout = Action.async { implicit request =>
    gotoLogoutSucceeded
  }

  def authenticate = Action.async { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => withMainModel { implicit mainModel =>
        BadRequest(html.login(formWithErrors))
      },
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

  override def registerView: Action[AnyContent] = AsyncStack(AuthorityKey -> Admin) { implicit request =>
    withMainModel { implicit mainModel =>
      Ok(html.register())
    }
  }
}