package controllers

import java.net.URL

import common.SupportedLang
import models.ui.{Comp, Tech}
import models.domain
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, AnyContent}
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

  override def addForm: Action[AnyContent] = Action {
    val action = AppLoader.routes.compController.save(None)
    Ok(views.html.compEdit(SupportedLang.defaultLang, None, action))
  }

  override def editForm(compId: String): Action[AnyContent] = Action.async {
    compService.get(compId).map { comp =>
      val action = AppLoader.routes.compController.save(Some(compId))
      Ok(views.html.compEdit(SupportedLang.defaultLang, Some(Comp(comp)), action))
    }
  }

  override def save(compId: Option[String]) = withForm(addCompForm) { form =>
    val result = if (compId.isEmpty) {
      compService.insert(form.name, new URL(form.website), form.location, form.codersCount, form.femaleCodersCount,
        form.note)
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
    val compsTechs = for {
      comps <- compService.all()
      techs <- techService.all()
    } yield (comps, techs)

    compsTechs.map { case (comps, techs) =>
      val uiComps = comps.map(Comp(_))

      Ok(views.html.comps(SupportedLang.defaultLang, uiComps, techs.map(Tech(_, None))))
    }
  }

  override def addTech(compId: String): Action[AnyContent] = withForm(addTechToCompForm) { form =>
    // Find tech ID for the name
    techService.all().map(_.find(_.name == form.techName)).flatMap {
      case Some(tech) => {
        compService.addTech(compId, tech.id, userId).map { Unit =>
          Redirect(AppLoader.routes.compController.all())
        }
      }
      case None => Future(BadRequest(s"Technology with name ${form.techName} doesn't exist!"))
    }

  }

  override def removeTech(compId: String, techId: String): Action[AnyContent] = Action.async { implicit request =>
    compService.removeTech(compId, techId, userId).map { Unit =>
      Redirect(AppLoader.routes.compController.all())
    }
  }
}