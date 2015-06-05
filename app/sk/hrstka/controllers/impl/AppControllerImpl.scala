package sk.hrstka.controllers.impl

import com.google.inject.{Inject, Singleton}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, Controller}
import sk.hrstka.controllers.AppController
import sk.hrstka.services.{LocationService, TechService}

@Singleton
class AppControllerImpl @Inject() (protected val locationService: LocationService,
                                   protected val techService: TechService,
                                   val messagesApi: MessagesApi) extends Controller with AppController {
  def untrail(path: String) = Action { MovedPermanently("/" + path) }
}
