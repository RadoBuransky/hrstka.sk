package controllers

import models.domain.User
import models.ui
import models.ui.MainModel
import play.api.mvc.Request
import services.{LocationService, TechService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait MainModelProvider {
  protected def withMainModel[A, R](city: Option[String] = None,
                                    tech: Option[String] = None,
                                    user: Option[User] = None)(action: (MainModel) => R)(implicit request: Request[A]): Future[R] = {
    locationService.all().flatMap { cities =>
      techService.all().map { techs =>
        action(MainModel(
          cities  = cities.map(ui.City(_)),
          techs   = techs.map(ui.Tech(_)),
          city    = city,
          tech    = tech,
          user    = user
        ))
      }
    }
  }

  protected def locationService: LocationService
  protected def techService: TechService
}
