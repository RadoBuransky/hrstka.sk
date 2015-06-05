package sk.hrstka.controllers.auth

import com.google.inject._
import play.api.mvc._
import sk.hrstka.controllers.auth.impl.AuthCompControllerImpl

case class AddCompForm(name: String,
                       website: String,
                       city: String,
                       employeeCount:Option[Int],
                       codersCount: Option[Int],
                       femaleCodersCount: Option[Int],
                       note: String,
                       products: Boolean,
                       services: Boolean,
                       internal: Boolean,
                       techs: List[String],
                       joel: List[Int])
case class AddTechToCompForm(techName: String)

@ImplementedBy(classOf[AuthCompControllerImpl])
trait AuthCompController {
  def addForm: Action[AnyContent]
  def editForm(compId: String): Action[AnyContent]
  def save(compId: Option[String]): Action[AnyContent]
}

