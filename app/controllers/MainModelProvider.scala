package controllers

import models.domain.User
import models.ui
import models.ui.MainModel
import play.api.Application
import play.api.mvc.Request
import services.{LocationService, TechService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait MainModelProvider {
  protected def withMainModel[A, R](city: Option[String] = None,
                                    tech: Option[String] = None,
                                    user: Option[User] = None)(action: (MainModel) => R)(implicit request: Request[A]): Future[R] = {
    locationService.all().flatMap { cities =>
      techService.allRatings().map { techRatings =>
        action(MainModel(
          cities  = cities.map(ui.CityFactory(_)),
          techs   = techRatings.map(techRating => ui.TechFactory(techRating.tech)),
          city    = city,
          tech    = tech,
          user    = user,
          mode    = application.mode
        ))
      }
    }
  }

  protected def locationService: LocationService
  protected def techService: TechService
  protected def application: Application
}
