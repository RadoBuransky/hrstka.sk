package controllers

import play.api.mvc._

trait AppController {
  def index: Action[AnyContent]
  def untrail(path: String): Action[AnyContent]
}

object AppController {
  def apply(): AppController = new AppControllerImpl()
}

private class AppControllerImpl extends Controller with AppController {
  def index = Action { Redirect("/tech") }
  def untrail(path: String) = Action { MovedPermanently("/" + path) }
}