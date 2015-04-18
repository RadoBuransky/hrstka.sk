package controllers

import java.net.URL

import common.SupportedLang
import models.{domain, ui}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, AnyContent, Call, Result}
import services.{CompService, TechService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class AddCompForm(name: String, website: String, location: String, codersCount: Option[Int],
                       femaleCodersCount: Option[Int], note: String)
case class AddTechToCompForm(techName: String)

trait CompController {
  def addForm: Action[AnyContent]
  def editForm(compId: String): Action[AnyContent]
  def save(compId: Option[String]): Action[AnyContent]
  def all: Action[AnyContent]
  def addTech(compId: String): Action[AnyContent]
  def removeTech(compId: String, techId: String): Action[AnyContent]
}

object CompController {
  val addCompForm = Form(
    mapping(
      "compName" -> text,
      "website" -> text,
      "location" -> text,
      "codersCount" -> optional(number),
      "femaleCodersCount" -> optional(number),
      "note" -> text
    )(AddCompForm.apply)(AddCompForm.unapply)
  )

  val addTechToCompForm = Form(
    mapping(
      "techName" -> text
    )(AddTechToCompForm.apply)(AddTechToCompForm.unapply)
  )

  def apply(compService: CompService,techService: TechService): CompController =
    new CompControllerImpl(compService, techService)
}

private class CompControllerImpl(compService: CompService,
                                 techService: TechService) extends BaseController with CompController {
  import controllers.CompController._

  override def addForm: Action[AnyContent] = Action.async {
    edit(None, AppLoader.routes.compController.save(None))
  }

  override def editForm(compId: String): Action[AnyContent] = Action.async {
    compService.get(compId).flatMap { comp =>
      edit(Some(comp), AppLoader.routes.compController.save(Some(compId)))
    }
  }

  private def edit(comp: Option[domain.Comp], action: Call): Future[Result] =
    techService.all().map { techs =>
      Ok(views.html.compEdit(SupportedLang.defaultLang, comp.map(ui.Comp.apply),  techs.map(ui.Tech(_, None)), action))
    }

  override def save(compId: Option[String]) = withForm(addCompForm) { form =>
    val result = if (compId.isEmpty) {
      compService.insert(form.name, new URL(form.website), form.location, form.codersCount, form.femaleCodersCount,
        form.note, userId)
    }
    else {
      compService.update(domain.Comp(
        id                = compId.get,
        name              = form.name,
        website           = new URL(form.website),
        location          = form.location,
        codersCount       = form.codersCount,
        femaleCodersCount = form.femaleCodersCount,
        note              = form.note
      ))
    }

    result.map { Unit =>
      Redirect(AppLoader.routes.compController.all())
    }
  }

  override def all =  Action.async { implicit request =>
    compService.all().map { comps =>
      Ok(views.html.comps(SupportedLang.defaultLang, comps.map(ui.Comp(_))))
    }
  }

  override def addTech(compId: String): Action[AnyContent] = withForm(addTechToCompForm) { form =>
    compService.addTech(form.techName, compId, userId).map { Unit =>
      Redirect(AppLoader.routes.compController.all())
    }
  }

  override def removeTech(compId: String, techId: String): Action[AnyContent] = Action.async { implicit request =>
    compService.removeTech(compId, techId, userId).map { Unit =>
      Redirect(AppLoader.routes.compController.all())
    }
  }
}