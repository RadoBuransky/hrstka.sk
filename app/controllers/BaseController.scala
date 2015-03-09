package controllers

import models.domain
import play.api.data.Form
import play.api.mvc.{Action, Controller, Result}

import scala.concurrent.Future

abstract class BaseController extends Controller {
  protected def userId: domain.Identifiable.Id = BaseController.userId1
  protected def withForm[T](form: Form[T])(action: T => Future[Result]) = Action.async { implicit request =>
    action(form.bindFromRequest().get)
  }
}

private object BaseController {
  val userId1 = "54ce855363ecfca285f788c8"
  val userId2 = "54fe02fea8000029af379322"
  val userId3 = "54fe02eea80000e2ae37931f"
}