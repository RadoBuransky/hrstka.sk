package sk.hrstka.controllers.impl

import com.google.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{Request, Action, AnyContent, Controller}
import sk.hrstka.common.Logging
import sk.hrstka.controllers.ApiController
import sk.hrstka.models.api.{Comp, CompFactory, JsonFormats, TechFactory}
import sk.hrstka.models.domain.CompRating
import sk.hrstka.services.{CompService, LocationService, TechService}

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
final class ApiControllerImpl @Inject() (compService: CompService,
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

  private def convertCompRating(compRating: CompRating)(implicit request: Request[AnyContent]): Comp =
    CompFactory.fromDomain(
      compRating,
      sk.hrstka.controllers.routes.ApiController.comp(compRating.comp.businessNumber.value).absoluteURL(),
      sk.hrstka.controllers.routes.CompController.get(compRating.comp.businessNumber.value).absoluteURL()
    )
}

private object ApiControllerImpl {
  val compsCacheKey = "api.companies"
}