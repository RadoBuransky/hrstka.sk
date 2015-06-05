package sk.hrstka.controllers.impl

import com.google.inject.{Inject, Singleton}
import jp.t2v.lab.play2.auth.AuthElement
import play.api.Application
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent}
import sk.hrstka.controllers._
import sk.hrstka.controllers.auth.impl.HrstkaAuthConfig
import sk.hrstka.models.domain.Admin
import sk.hrstka.services.{AuthService, LocationService, TechService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class AuthControllerImpl @Inject() (protected val authService: AuthService,
                                    protected val locationService: LocationService,
                                    protected val techService: TechService,
                                    protected val application: Application,
                                    val messagesApi: MessagesApi)
  extends BaseController with AuthController with MainModelProvider with HrstkaAuthConfig with AuthElement {
  val loginForm = Form(mapping("email" -> email, "password" -> text)(LoginForm.apply)(LoginForm.unapply))
  val registerForm = Form(mapping(
    "email" -> email,
    "password" -> text,
    "passwordAgain" -> text)(RegisterForm.apply)(RegisterForm.unapply))

  def login = Action.async { implicit request =>
    withMainModel() { implicit mainModel =>
      Ok(sk.hrstka.views.html.login(loginForm))
    }
  }

  def logout = Action.async { implicit request =>
    gotoLogoutSucceeded
  }

  def authenticate = Action.async { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => withMainModel() { implicit mainModel =>
        BadRequest(sk.hrstka.views.html.login(formWithErrors))
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
          Redirect(sk.hrstka.controllers.routes.CompController.all())
        }
      case _ => Future.successful(BadRequest("Passwords do not match!"))
    }
  }

  override def registerView: Action[AnyContent] = AsyncStack(AuthorityKey -> Admin) { implicit request =>
    withMainModel(None, None, Some(loggedIn)) { implicit mainModel =>
      Ok(sk.hrstka.views.html.register())
    }
  }
}
