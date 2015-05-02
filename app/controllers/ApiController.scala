package controllers

import play.api.libs.json.Json
import play.api.mvc._
import services.{TechService, CompService}
import scala.concurrent.ExecutionContext.Implicits.global

trait ApiController {
  def comps(): Action[AnyContent]
  def techs(): Action[AnyContent]
}

object ApiController {
  def apply(compService: CompService, techService: TechService): ApiController
    = new ApiControllerImpl(compService, techService)
}

private class ApiControllerImpl(compService: CompService,
                                techService: TechService) extends BaseController with ApiController {
  import models.api.JsonFormats._

  override def comps(): Action[AnyContent] = Action.async { implicit request =>
    compService.all().map { comps =>
      Ok(Json.toJson(comps.map(models.api.Comp.fromDomain(_))))
    }
  }

  override def techs(): Action[AnyContent] = Action.async { implicit request =>
    techService.all().map { techs =>
      Ok(Json.toJson(techs.map(models.api.Tech.fromDomain(_))))
    }
  }
}