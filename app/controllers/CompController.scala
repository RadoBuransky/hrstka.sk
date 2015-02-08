package controllers

import java.net.URL

import common.SupportedLang
import models.ui.{Tech, Comp}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, AnyContent}
import services.{TechService, CompService}

import scala.concurrent.ExecutionContext.Implicits.global

case class InsertCompForm(compName: String, website: String)

trait CompController {
  def insert: Action[AnyContent]
  def all: Action[AnyContent]
}

object CompController {
  val insertCompForm = Form(
    mapping(
      "compName" -> text,
      "website" -> text
    )(InsertCompForm.apply)(InsertCompForm.unapply)
  )

  def apply(compService: CompService,techService: TechService): CompController =
    new CompControllerImpl(compService, techService)
}

private class CompControllerImpl(compService: CompService,
                                 techService: TechService) extends BaseController with CompController {
  import controllers.CompController._

  override def insert = withForm(insertCompForm) { form =>
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
      } yield Comp(comp, canVoteUp =  true, canVoteDown = true)

      val uiTechs = for {
        tech <- techs
      } yield Tech(tech, canVoteUp = false, canVoteDown = false)

      Ok(views.html.companies(SupportedLang.defaultLang, uiComps, uiTechs))
    }
  }
}