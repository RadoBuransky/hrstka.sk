package controllers

import java.net.URL

import common.SupportedLang
import models.ui.{Comp, Tech}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, AnyContent}
import services.{CompService, TechService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class AddCompForm(compName: String, website: String)
case class AddTechToCompForm(techName: String)

trait CompController {
  def add: Action[AnyContent]
  def all: Action[AnyContent]
  def addTech(compId: String): Action[AnyContent]
  def removeTech(compId: String, techId: String): Action[AnyContent]
}

object CompController {
  val addCompForm = Form(
    mapping(
      "compName" -> text,
      "website" -> text
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

  override def add = withForm(addCompForm) { form =>
    compService.insert(form.compName, new URL(form.website), userId).map { Unit =>
      Redirect(AppLoader.routes.compController.all())
    }
  }

  override def all =  Action.async { implicit request =>
    val compsTechs = for {
      comps <- compService.all()
      techs <- techService.all()
    } yield (comps, techs)

    compsTechs.map { case (comps, techs) =>
      val uiComps = for {
        comp <- comps
      } yield Comp(comp, canVoteUp =  true, canVoteDown = true, comp.techs)

      Ok(views.html.companies(SupportedLang.defaultLang, uiComps, techs.map(Tech(_, None))))
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