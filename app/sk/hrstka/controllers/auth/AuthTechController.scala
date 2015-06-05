package sk.hrstka.controllers.auth

import com.google.inject.ImplementedBy
import play.api.mvc.{Action, AnyContent}
import sk.hrstka.controllers.auth.impl.AuthTechControllerImpl

case class AddTechForm(categoryHandle: String,
                       name: String,
                       website: String)

@ImplementedBy(classOf[AuthTechControllerImpl])
trait AuthTechController {
  def all: Action[AnyContent]
  def add: Action[AnyContent]
  def voteUp(id: String): Action[AnyContent]
  def voteDown(id: String): Action[AnyContent]
}

