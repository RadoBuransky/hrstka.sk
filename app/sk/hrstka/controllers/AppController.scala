package sk.hrstka.controllers

import com.google.inject._
import play.api.mvc._
import sk.hrstka.controllers.impl.AppControllerImpl

@ImplementedBy(classOf[AppControllerImpl])
trait AppController {
  def untrail(path: String): Action[AnyContent]
}

