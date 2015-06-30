package sk.hrstka.controllers.impl

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Controller, Request}
import sk.hrstka.common.Logging
import sk.hrstka.controllers.ApiController
import sk.hrstka.models.api._
import sk.hrstka.models.domain.{CompRating, TechRating}
import sk.hrstka.services.{CompService, LocationService, TechService}

import scala.concurrent.ExecutionContext.Implicits.global

final class ApiControllerImpl(compService: CompService,
                              techService: TechService,
                              locationService: LocationService)
  extends Controller with ApiController with Logging {
  import JsonFormats._

  override def comps() = Action.async { implicit request =>
    compService.all(None, None).map { compRatings =>
      Ok(Json.toJson(compRatings.map(convertCompRating)))
    }
  }

  override def comp(businessNumber: String) = Action.async { implicit request =>
    compService.all(None, None).map { compRatings =>
      compRatings.find(_.comp.businessNumber.value == businessNumber) match {
        case Some(compRating) => Ok(Json.toJson(convertCompRating(compRating)))
        case None => NotFound(s"Company with business number [$businessNumber] does not exist!")
      }
    }
  }

  override def techs() = Action.async { implicit request =>
    techService.allRatings().map { techRatings =>
      Ok(Json.toJson(techRatings.map(convertTechRating)))
    }
  }

  override def tech(handle: String) = Action.async { implicit request =>
    techService.allRatings().map { techRatings =>
      techRatings.find(_.tech.handle.value == handle) match {
        case Some(techRating) => Ok(Json.toJson(convertTechRating(techRating)))
        case None => NotFound(s"Technology with handle [$handle] does not exist!")
      }
    }
  }

  override def cities() = Action.async { implicit request =>
    locationService.all().map { cities =>
      Ok(Json.toJson(cities.map { city =>
        Json.obj(
          "handle" -> city.handle.value,
          "sk" -> city.sk
        )
      }))
    }
  }

  private def convertTechRating(techRating: TechRating)(implicit request: Request[AnyContent]): Tech =
    TechFactory.fromDomain(
      techRating,
      sk.hrstka.controllers.routes.ApiController.tech(techRating.tech.handle.value).absoluteURL()
    )

  private def convertCompRating(compRating: CompRating)(implicit request: Request[AnyContent]): Comp =
    CompFactory.fromDomain(
      compRating,
      sk.hrstka.controllers.routes.CompController.get(compRating.comp.businessNumber.value).absoluteURL()
    )
}

private object ApiControllerImpl {
  val compsCacheKey = "api.companies"
}