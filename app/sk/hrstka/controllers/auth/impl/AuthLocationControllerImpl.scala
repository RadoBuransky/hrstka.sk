package sk.hrstka.controllers.auth.impl

import com.google.inject.{Inject, Singleton}
import play.api.Application
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesApi
import play.api.mvc._
import sk.hrstka.controllers.auth.{AddCityForm, AuthLocationController}
import sk.hrstka.controllers.impl.{BaseController, MainModelProvider}
import sk.hrstka.models.domain._
import sk.hrstka.models.ui.{CityFactory, CountryFactory}
import sk.hrstka.services.{AuthService, LocationService, TechService}

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
final class AuthLocationControllerImpl @Inject() (protected val authService: AuthService,
                                                  protected val locationService: LocationService,
                                                  protected val techService: TechService,
                                                  protected val application: Application,
                                                  val messagesApi: MessagesApi)
  extends BaseController with MainModelProvider with HrstkaAuthConfig with HrstkaAuthElement with AuthLocationController {
  override def all: Action[AnyContent] = AsyncStack(AuthorityKey -> Eminent) { implicit request =>
    locationService.countries().flatMap { allCountries =>
      val uiCountries = allCountries.map(CountryFactory.apply)
      locationService.usedCities().flatMap { cities =>
        val uiCities = cities.map(CityFactory.apply)
        val citiesForCountries = uiCities.groupBy(_.country).toSeq
        withMainModel(None, None, Some(loggedIn)) { implicit mainModel =>
          Ok(sk.hrstka.views.html.auth.cities(uiCountries, citiesForCountries))
        }
      }
    }
  }

  override def add: Action[AnyContent] = AsyncStack(AuthorityKey -> Eminent) { implicit request =>
    withForm(AuthLocationControllerImpl.addCityForm) { form =>
      locationService.getCountryByCode(Iso3166(form.countryCode)).flatMap { country =>
        locationService.upsert(City(
          handle = HandleFactory.fromHumanName(form.city),
          en = form.city,
          country = country
        )).map { _ =>
          Redirect(sk.hrstka.controllers.auth.routes.AuthLocationController.all())
        }
      }
    }
  }

  override def remove(handle: String): Action[AnyContent] = AsyncStack(AuthorityKey -> Eminent) { implicit request =>
    locationService.remove(Handle(handle)).map { _ =>
      Redirect(sk.hrstka.controllers.auth.routes.AuthLocationController.all())
    }
  }
}

private object AuthLocationControllerImpl {
  val addCityForm = Form(
    mapping(
      "countryCode" -> text,
      "city"        -> text
    )(AddCityForm.apply)(AddCityForm.unapply)
  )
}