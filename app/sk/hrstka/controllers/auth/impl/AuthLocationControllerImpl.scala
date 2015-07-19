package sk.hrstka.controllers.auth.impl

import com.google.inject.{Inject, Singleton}
import jp.t2v.lab.play2.auth.OptionalAuthElement
import play.api.Application
import play.api.i18n.MessagesApi
import play.api.mvc._
import sk.hrstka.controllers.auth.AuthLocationController
import sk.hrstka.controllers.impl.{BaseController, MainModelProvider}
import sk.hrstka.models.ui.CityFactory
import sk.hrstka.services.{AuthService, LocationService, TechService}

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
final class AuthLocationControllerImpl @Inject() (protected val authService: AuthService,
                                                  protected val locationService: LocationService,
                                                  protected val techService: TechService,
                                                  protected val application: Application,
                                                  val messagesApi: MessagesApi)
  extends BaseController with MainModelProvider with HrstkaAuthConfig with OptionalAuthElement with AuthLocationController {
  override def all: Action[AnyContent] = AsyncStack { implicit request =>
    locationService.cities().flatMap { cities =>
      val uiCities = cities.map(CityFactory.apply)
      val uiCountries = uiCities.map(_.country).distinct
      val citiesMap = uiCities.groupBy(_.country)
      withMainModel(None, None, loggedIn) { implicit mainModel =>
        Ok(sk.hrstka.views.html.auth.locations(uiCountries, citiesMap))
      }
    }
  }

  override def remove(handle: String): Action[AnyContent] = ???

  override def add: Action[AnyContent] = ???
}