package sk.hrstka.controllers.auth.impl

import com.google.inject.{Inject, Singleton}
import jp.t2v.lab.play2.auth.OptionalAuthElement
import play.api.Application
import play.api.i18n.MessagesApi
import play.api.mvc._
import sk.hrstka.controllers.auth.AuthLocationController
import sk.hrstka.controllers.impl.{BaseController, MainModelProvider}
import sk.hrstka.services.{AuthService, LocationService, TechService}

@Singleton
final class AuthLocationControllerImpl @Inject() (protected val authService: AuthService,
                                                  protected val locationService: LocationService,
                                                  protected val techService: TechService,
                                                  protected val application: Application,
                                                  val messagesApi: MessagesApi)
  extends BaseController with MainModelProvider with HrstkaAuthConfig with OptionalAuthElement with AuthLocationController {
  override def all: Action[AnyContent] = AsyncStack { implicit request =>
    withMainModel(None, None, loggedIn) { implicit mainModel =>
      Ok(sk.hrstka.views.html.auth.locations())
    }
  }

  override def remove(handle: String): Action[AnyContent] = ???

  override def add: Action[AnyContent] = ???
}