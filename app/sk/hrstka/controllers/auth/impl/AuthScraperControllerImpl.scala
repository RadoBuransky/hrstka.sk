package sk.hrstka.controllers.auth.impl

import javax.inject.{Inject, Singleton}

import play.api.Application
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent}
import sk.hrstka.controllers.auth.AuthScraperController
import sk.hrstka.controllers.impl.{BaseController, MainModelProvider}
import sk.hrstka.models.domain.Eminent
import sk.hrstka.services.{AuthService, LocationService, ScrapingService, TechService}

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class AuthScraperControllerImpl @Inject() (protected val authService: AuthService,
                                           protected val locationService: LocationService,
                                           protected val techService: TechService,
                                           protected val application: Application,
                                           val messagesApi: MessagesApi,
                                           val scrapingService: ScrapingService)
  extends BaseController with AuthScraperController with MainModelProvider  with HrstkaAuthConfig with HrstkaAuthElement  {

  override def scrape(): Action[AnyContent] = AsyncStack(AuthorityKey -> Eminent) { implicit request =>
    scrapingService.scrape.flatMap { scrapingResult =>
      withMainModel(Some(loggedIn)) { implicit mainModel =>
        val text = scrapingResult.companies.map { c =>
          c.name + " " + c.isNew
        }
        Ok(sk.hrstka.views.html.auth.scrape(text))
      }
    }
  }
}