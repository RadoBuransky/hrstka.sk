package controllers

import common.SupportedLang
import play.api.mvc._

trait TechController {
  def add(): Action[AnyContent]
  def all(): Action[AnyContent]
}

object TechController {
  def apply(): TechController = new TechControllerImpl
}

private class TechControllerImpl extends Controller with TechController {
  override def add(): Action[AnyContent] = ???

  override def all(): Action[AnyContent] = Action {
    Ok(views.html.technologies(SupportedLang.defaultLang))
  }
}