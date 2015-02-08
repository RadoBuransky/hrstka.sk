package controllers

import java.net.URL

import common.SupportedLang
import models.ui.Comp
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, AnyContent}
import services.CompService

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

  def apply(compService: CompService): CompController = new CompControllerImpl(compService)
}

private class CompControllerImpl(compService: CompService) extends BaseController with CompController {
  import controllers.CompController._

  override def insert = withForm(insertCompForm) { form =>
    compService.insert(form.compName, new URL(form.website), userId).map { Unit =>
      Redirect(AppLoader.routes.compController.all())
    }
  }

  override def all =  Action.async { implicit request =>
    compService.all().map { comps =>
      val uiComps = comps.map { comp =>
        Comp(comp, canVoteUp =  true, canVoteDown = true)
      }
      Ok(views.html.companies(SupportedLang.defaultLang, uiComps))
    }
  }
}