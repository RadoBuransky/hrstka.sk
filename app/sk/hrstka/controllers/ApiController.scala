package sk.hrstka.controllers

import com.google.inject._
import play.api.mvc._
import sk.hrstka.controllers.impl.ApiControllerImpl

@ImplementedBy(classOf[ApiControllerImpl])
trait ApiController {
  def comps(): Action[AnyContent]
  def techs(): Action[AnyContent]
  def cities(): Action[AnyContent]
}

