package sk.hrstka.controllers.auth.impl

import com.google.inject.{Inject, Singleton}
import jp.t2v.lab.play2.auth.{AuthConfig, LoginLogout}
import play.api.Application
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent}
import sk.hrstka.controllers.auth.{AuthController, LoginForm, RegisterForm}
import sk.hrstka.controllers.impl.{BaseController, MainModelProvider}
import sk.hrstka.models.domain.{Admin, Email}
import sk.hrstka.services.{AuthService, LocationService, TechService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
final class AuthControllerImpl @Inject() (protected val authService: AuthService,
                                          protected val locationService: LocationService,
                                          protected val techService: TechService,
                                          protected val application: Application,
                                          val messagesApi: MessagesApi)
  extends BaseController with AuthController with MainModelProvider with HrstkaAuthConfig with HrstkaAuthElement with LoginLogout with AuthConfig {
  import AuthControllerImpl._

  def login = Action.async { implicit request =>
    withMainModel() { implicit mainModel =>
      Ok(sk.hrstka.views.html.auth.login(loginForm))
    }
  }

  def logout = Action.async { implicit request =>
    gotoLogoutSucceeded
  }

  def authenticate = Action.async { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(formWithErrors.errorsAsJson)),
      loginForm => {
        authService.authenticate(Email(loginForm.email), loginForm.password).flatMap {
          case Some(user) => gotoLoginSucceeded(user.email.value)
          case _ => Future.successful(Unauthorized)
        }
      }
    )
  }

  override def registerView: Action[AnyContent] = AsyncStack(AuthorityKey -> Admin) { implicit request =>
    withMainModel(Some(loggedIn)) { implicit mainModel =>
      Ok(sk.hrstka.views.html.auth.register())
    }
  }

  override def register: Action[AnyContent] = AsyncStack(AuthorityKey -> Admin) { implicit request =>
    val form = registerForm.bindFromRequest.get
    form.password match {
      case form.passwordAgain =>
        authService.createUser(Email(form.email), form.password).map { _ =>
          Redirect(sk.hrstka.controllers.routes.CompController.search())
        }
      case _ => Future.successful(BadRequest("Passwords do not match!"))
    }
  }
}

object AuthControllerImpl {
  val loginForm = Form(mapping(
      "email" -> email,
      "password" -> text)
      (LoginForm.apply)(LoginForm.unapply)
  )
  val registerForm = Form(mapping(
    "email" -> email,
    "password" -> text,
    "passwordAgain" -> text)(RegisterForm.apply)(RegisterForm.unapply))
}