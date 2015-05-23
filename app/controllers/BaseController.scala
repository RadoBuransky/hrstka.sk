package controllers

import models.domain
import play.api.data.Form
import play.api.mvc.{Request, Action, Controller, Result}

import scala.concurrent.Future

abstract class BaseController extends Controller {
  protected def userId: domain.Identifiable.Id = BaseController.userId1
  protected def withForm[T, A](form: Form[T])(action: T => Future[Result])(implicit request: Request[A]) =
    form.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(formWithErrors.errorsAsJson)) ,
      f => action(f)
    )
}

private object BaseController {
  val userId1 = "54ce855363ecfca285f788c8"
  val userId2 = "54fe02fea8000029af379322"
  val userId3 = "54fe02eea80000e2ae37931f"
}