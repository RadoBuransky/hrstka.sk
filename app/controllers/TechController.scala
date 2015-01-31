package controllers

import common.SupportedLang
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._

case class AddTechForm(techName: String)

trait TechController {
  def add: Action[AnyContent]
  def all: Action[AnyContent]
}

object TechController {
  val addTechForm = Form(
    mapping(
      "techName" -> text
    )(AddTechForm.apply)(AddTechForm.unapply)
  )

  def apply(): TechController = new TechControllerImpl
}

private class TechControllerImpl extends Controller with TechController {
  import TechController._

  override def add: Action[AnyContent] = Action { implicit request =>
    val addTech = addTechForm.bindFromRequest.get
    Ok(views.html.technologies(SupportedLang.defaultLang, addTech.techName + " added"))
  }

  override def all: Action[AnyContent] = Action {
    Ok(views.html.technologies(SupportedLang.defaultLang, ""))
  }
}