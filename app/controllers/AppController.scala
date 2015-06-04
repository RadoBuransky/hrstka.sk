package controllers

import com.google.inject._
import play.api.i18n.MessagesApi
import play.api.mvc._
import sk.hrstka.services.{LocationService, TechService}

@ImplementedBy(classOf[AppControllerImpl])
trait AppController {
  def untrail(path: String): Action[AnyContent]
}

@Singleton
class AppControllerImpl @Inject() (protected val locationService: LocationService,
                                   protected val techService: TechService,
                                   val messagesApi: MessagesApi) extends Controller with AppController {
  def untrail(path: String) = Action { MovedPermanently("/" + path) }
}