package controllers

import play.api.i18n.MessagesApi
import play.api.mvc._
import services.{LocationService, TechService}

trait AppController {
  def untrail(path: String): Action[AnyContent]
}

class AppControllerImpl(protected val locationService: LocationService,
                        protected val techService: TechService,
                        val messagesApi: MessagesApi) extends Controller with AppController {
  def untrail(path: String) = Action { MovedPermanently("/" + path) }
}