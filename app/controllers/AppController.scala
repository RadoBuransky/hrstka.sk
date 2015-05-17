package controllers

import play.api.mvc._
import services.{LocationService, TechService}

trait AppController {
  def index: Action[AnyContent]
  def untrail(path: String): Action[AnyContent]
}

class AppControllerImpl(protected val locationService: LocationService,
                        protected val techService: TechService) extends Controller with AppController with MainModelProvider {
  def index = Action.async { implicit request =>
    withMainModel { implicit mainModel =>
      Ok(views.html.index())
    }
  }
  def untrail(path: String) = Action { MovedPermanently("/" + path) }
}