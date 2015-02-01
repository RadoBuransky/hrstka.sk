package controllers

import common.SupportedLang
import play.api.Routes
import play.api.data.Forms._
import play.api.data._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import services.TechService

case class AddTechForm(techName: String)

trait TechController {
  def insert: Action[AnyContent]
  def all: Action[AnyContent]
}

object TechController {
  val insertTechForm = Form(
    mapping(
      "techName" -> text
    )(AddTechForm.apply)(AddTechForm.unapply)
  )

  def apply(techService: TechService): TechController = new TechControllerImpl(techService)
}

private class TechControllerImpl(techService: TechService) extends BaseController with TechController {
  import controllers.TechController._

  override def insert: Action[AnyContent] = Action.async { implicit request =>
    val form = insertTechForm.bindFromRequest().get
    techService.insert(form.techName, userId).map { Unit =>
      Redirect(AppLoader.routes.techController.all())
    }
  }

  override def all: Action[AnyContent] = Action.async { implicit request =>
    techService.all().map { techs =>
      Ok(views.html.technologies(SupportedLang.defaultLang, None, techs))
    }
  }
}