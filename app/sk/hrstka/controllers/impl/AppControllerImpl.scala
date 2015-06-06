package sk.hrstka.controllers.impl

import com.google.inject.Singleton
import play.api.mvc.{Action, Controller}
import sk.hrstka.controllers.AppController

@Singleton
class AppControllerImpl() extends Controller with AppController {
  def untrail(path: String) = Action { MovedPermanently("/" + path) }
}
