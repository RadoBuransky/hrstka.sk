package sk.hrstka.controllers.auth.impl

import java.net.URI
import javax.inject.{Inject, Singleton}

import play.api.Application
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent}
import sk.hrstka.controllers.auth.AuthScraperController
import sk.hrstka.controllers.impl.{BaseController, MainModelProvider}
import sk.hrstka.models.domain.Eminent
import sk.hrstka.models.ui.ScrapedCompFactory
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
        val scrapedComps = scrapingResult.companies.map { scrapedComp =>
          val hrstkaUrl = scrapedComp.businessNumber match {
            case Some(businessNumber) => sk.hrstka.controllers.auth.routes.AuthCompController.editForm(businessNumber.value)
            case None => sk.hrstka.controllers.auth.routes.AuthCompController.addForm()
          }

          ScrapedCompFactory(scrapedComp, new URI(hrstkaUrl.url))
        }
        Ok(sk.hrstka.views.html.auth.scrape(scrapedComps))
      }
    }
  }
}