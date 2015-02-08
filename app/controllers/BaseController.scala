package controllers

import models.domain
import play.api.data.Form
import play.api.mvc.{Action, Controller, Result}

import scala.concurrent.Future

abstract class BaseController extends Controller {
  protected def userId: domain.Identifiable.Id = "54ce855363ecfca285f788c8"
  protected def withForm[T](form: Form[T])(action: T => Future[Result]) = Action.async { implicit request =>
    action(form.bindFromRequest().get)
  }
}
