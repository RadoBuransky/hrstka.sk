package sk.hrstka.controllers.impl

import play.api.Application
import play.api.mvc._
import sk.hrstka.models.domain.{Handle, User}
import sk.hrstka.models.ui.{CityFactory, MainModel, TechRatingFactory}
import sk.hrstka.services.{LocationService, TechService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait MainModelProvider {
  protected def withMainModel[A, R](city: Option[String] = None,
                                    tech: Option[String] = None,
                                    user: Option[User] = None)(action: (MainModel) => R)(implicit request: Request[A]): Future[R] = {
    locationService.all().flatMap { cities =>
      techService.allUsedRatings(city.map(Handle)).map { techRatings =>
        action(MainModel(
          cities        = cities.map(CityFactory(_)),
          techRatings   = techRatings.map(TechRatingFactory.apply),
          city          = city,
          tech          = tech,
          user          = user,
          mode          = application.mode
        ))
      }
    }
  }

  protected def locationService: LocationService
  protected def techService: TechService
  protected def application: Application
}
