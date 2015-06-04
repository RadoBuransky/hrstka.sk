package sk.hrstka.controllers

import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Controller, Request, Result}

import scala.concurrent.Future

abstract class BaseController extends Controller with I18nSupport {
  protected def withForm[T, A](form: Form[T])(action: T => Future[Result])(implicit request: Request[A]) =
    form.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(formWithErrors.errorsAsJson)) ,
      f => action(f)
    )
}