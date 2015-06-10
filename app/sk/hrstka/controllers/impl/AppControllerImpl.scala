package sk.hrstka.controllers.impl

import com.google.inject.{Inject, Singleton}
import play.api.Application
import play.api.mvc.{AnyContent, Action, Controller}
import sk.hrstka.controllers.AppController
import sk.hrstka.services.{TechService, LocationService}

@Singleton
final class AppControllerImpl @Inject() (protected val locationService: LocationService,
                                         protected val techService: TechService,
                                         protected val application: Application) extends Controller with AppController with MainModelProvider {
  def untrail(path: String) = Action { MovedPermanently("/" + path) }

  override def api(): Action[AnyContent] = Action.async { implicit request =>
    withMainModel() { implicit mainModel =>
      Ok(sk.hrstka.views.html.api())
    }
  }

  override def about(): Action[AnyContent] = Action.async { implicit request =>
    withMainModel() { implicit mainModel =>
      Ok(sk.hrstka.views.html.about())
    }
  }

  override def addCompInfo(): Action[AnyContent] = Action.async { implicit request =>
    withMainModel() { implicit mainModel =>
      Ok(sk.hrstka.views.html.addCompInfo())
    }
  }
}
