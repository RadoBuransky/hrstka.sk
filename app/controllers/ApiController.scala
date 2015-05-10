package controllers

import play.api.libs.json.Json
import play.api.mvc._
import services.{CompService, LocationService, TechService}

import scala.concurrent.ExecutionContext.Implicits.global

trait ApiController {
  def comps(): Action[AnyContent]
  def techs(): Action[AnyContent]
  def cities(): Action[AnyContent]
}

class ApiControllerImpl(compService: CompService,
                        techService: TechService,
                        locationService: LocationService) extends BaseController with ApiController {
  import models.api.JsonFormats._

  override def comps(): Action[AnyContent] = Action.async { implicit request =>
    compService.all().map { comps =>
      Ok(Json.toJson(comps.map(models.api.Comp.fromDomain)))
    }
  }

  override def techs(): Action[AnyContent] = Action.async { implicit request =>
    techService.all().map { techs =>
      Ok(Json.toJson(techs.map(models.api.Tech.fromDomain)))
    }
  }

  override def cities(): Action[AnyContent] = Action.async { implicit request =>
    locationService.all().map { cities =>
      Ok(Json.toJson(cities.map { city =>
        Json.obj(
          "handle" -> city.handle.value,
          "sk" -> city.sk
        )
      }))
    }
  }
}