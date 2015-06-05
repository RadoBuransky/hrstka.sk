package sk.hrstka.controllers

import com.google.inject.ImplementedBy
import play.api.mvc._
import sk.hrstka.controllers.impl.CompControllerImpl

@ImplementedBy(classOf[CompControllerImpl])
trait CompController {
  def get(compId: String): Action[AnyContent]
  def women: Action[AnyContent]
  def all: Action[AnyContent]
  def cityTech(cityHandle: String, techHandle: String): Action[AnyContent]
}

