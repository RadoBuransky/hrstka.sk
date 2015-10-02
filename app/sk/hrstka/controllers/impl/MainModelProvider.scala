package sk.hrstka.controllers.impl

import play.api.mvc._
import play.api.{Application, Mode}
import sk.hrstka.controllers.auth.impl.HrstkaAuthElement
import sk.hrstka.models.domain.User
import sk.hrstka.models.ui._
import sk.hrstka.services.{LocationService, TechService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait MainModelProvider {
  protected def withMainModel[A, R](user: Option[User] = None,
                                    title: String = MainModelSingleton.defaultTitle,
                                    description: String = MainModelSingleton.defaultDescription,
                                    searchQuery: String = "")(action: (MainModel) => R)(implicit request: Request[A]): Future[R] = {
    // Faku user for convenient development
    val devUser = user match {
      case Some(u) => Some(u)
      case None => application.mode match {
        case Mode.Dev => Some(HrstkaAuthElement.devUser)
        case _ => None
      }
    }

    locationService.usedCities().flatMap { cities =>
      techService.allRatings().map { techRatings =>
        action(MainModel(
          cities        = cities.map(CityFactory(_)),
          techRatings   = techRatings.map(TechRatingFactory.apply),
          user          = devUser,
          mode          = application.mode,
          title         = title,
          description   = description,
          searchQuery   = searchQuery
        ))
      }
    }
  }

  protected def locationService: LocationService
  protected def techService: TechService
  protected def application: Application
}
