package controllers

import com.google.inject._
import play.api.libs.json.Json
import play.api.mvc._
import sk.hrstka.models.api.{CompFactory, JsonFormats, TechFactory}
import sk.hrstka.services.{CompService, LocationService, TechService}

import scala.concurrent.ExecutionContext.Implicits.global

@ImplementedBy(classOf[ApiControllerImpl])
trait ApiController {
  def comps(): Action[AnyContent]
  def techs(): Action[AnyContent]
  def cities(): Action[AnyContent]
}

@Singleton
final class ApiControllerImpl @Inject() (compService: CompService,
                                         techService: TechService,
                                         locationService: LocationService) extends Controller with ApiController {
  import JsonFormats._

  override def comps(): Action[AnyContent] = Action.async { implicit request =>
    compService.all().map { comps =>
      Ok(Json.toJson(comps.map(CompFactory.fromDomain)))
    }
  }

  override def techs(): Action[AnyContent] = Action.async { implicit request =>
    techService.allRatings().map { techRatings =>
      Ok(Json.toJson(techRatings.map(TechFactory.fromDomain)))
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