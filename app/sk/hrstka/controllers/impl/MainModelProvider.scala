package sk.hrstka.controllers.impl

import play.api.Application
import play.api.mvc._
import sk.hrstka.models.domain.{Handle, User}
import sk.hrstka.models.ui.{TechRating, CityFactory, MainModel, TechRatingFactory}
import sk.hrstka.services.{LocationService, TechService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait MainModelProvider {
  protected def withMainModel[A, R](city: Option[String] = None,
                                    tech: Option[String] = None,
                                    user: Option[User] = None)(action: (MainModel) => R)(implicit request: Request[A]): Future[R] = {
    locationService.cities().flatMap { cities =>
      techService.allUsedRatings(city.map(Handle)).map { techRatings =>
        action(MainModel(
          cities        = cities.map(CityFactory(_)),
          techRatings   = setOpacity(techRatings.map(TechRatingFactory.apply)),
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

  private def setOpacity(techRatings: Seq[TechRating]): Seq[TechRating] = {
    val (top10perc, bottom90perc) = techRatings.splitAt((techRatings.size * 0.1).round.toInt)
    val (middle60perc, bottom30perc) = bottom90perc.splitAt((techRatings.size * 0.6).round.toInt)

    // Top 10% have no transparency
    val top10percOpacity = top10perc.map(_.copy(opacity = 1.0))

    // Bottom 30% have 30% transparency
    val bottom30percOpacity = bottom30perc.map(_.copy(opacity = 0.3))

    // The middle gets gradually transparent
    val step = (1.0 - 0.3) / middle60perc.size.toDouble
    val middleOpacity = middle60perc.zipWithIndex.map {
      case (techRating, index) => techRating.copy(opacity = 1.0 - index*step)
    }

    // Result
    top10percOpacity ++ middleOpacity ++ bottom30percOpacity
  }
}
