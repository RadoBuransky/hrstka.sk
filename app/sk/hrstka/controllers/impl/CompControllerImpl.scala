package sk.hrstka.controllers.impl

import com.google.inject.{Inject, Singleton}
import jp.t2v.lab.play2.auth.OptionalAuthElement
import play.api.Application
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent}
import sk.hrstka
import sk.hrstka.controllers.CompController
import sk.hrstka.controllers.auth.impl.HrstkaAuthConfig
import sk.hrstka.models.domain.{City, Handle, Id, Tech}
import sk.hrstka.models.ui.CompFactory
import sk.hrstka.services.{AuthService, CompService, LocationService, TechService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class CompControllerImpl @Inject() (compService: CompService,
                                    protected val authService: AuthService,
                                    protected val techService: TechService,
                                    protected val locationService: LocationService,
                                    protected val application: Application,
                                    val messagesApi: MessagesApi)
  extends BaseController with CompController with MainModelProvider with HrstkaAuthConfig with OptionalAuthElement {

  override def get(compId: String): Action[AnyContent] = AsyncStack { implicit request =>
    for {
      comp    <- compService.get(Id(compId))
      result  <- withMainModel(None, None, loggedIn) { implicit mainModel =>
        Ok(sk.hrstka.views.html.comp(CompFactory(comp)))
      }
    } yield result
  }

  override def women: Action[AnyContent] = AsyncStack { implicit request =>
    for {
      topWomen  <- compService.topWomen()
      result    <- withMainModel(None, None, loggedIn) { implicit mainModel =>
        Ok(sk.hrstka.views.html.women(topWomen.map(hrstka.models.ui.CompFactory(_))))
      }
    } yield result
  }

  override def all = cityTech("", "")
  override def cityTech(cityHandle: String, techHandle: String) =
    cityTechAction(Option(cityHandle).filter(_.trim.nonEmpty), Option(techHandle).filter(_.trim.nonEmpty))

  private def cityTechAction(cityHandle: Option[String], techHandle: Option[String]) = AsyncStack { implicit request =>
    for {
      city    <- cityForHandle(cityHandle)
      tech    <- techForHandle(techHandle)
      comps   <- compService.all(city.map(_.handle), tech.map(_.handle))
      result  <- withMainModel(cityHandle, techHandle, loggedIn) { implicit mainModel =>
        Ok(sk.hrstka.views.html.index(
          headline(city, tech),
          comps.map(hrstka.models.ui.CompFactory(_))))
      }
    } yield result
  }

  private def headline(city: Option[City], tech: Option[Tech]): String = {
    val cityHeadline = city
      .map(c => " v meste " + c.sk)
      .getOrElse("")
    val techHeadline = tech.map(_.name).getOrElse("")
    if (city.isEmpty && tech.isEmpty)
      "Firmy na Slovensku kde sa programuje"
    else
      if (tech.isEmpty)
        "Firmy" + cityHeadline
      else
        techHeadline + cityHeadline
  }

  private def techForHandle(techHandle: Option[String]): Future[Option[Tech]] = techHandle match {
    case Some(handle) => techService.getByHandle(Handle(handle)).map(Some(_))
    case None => Future.successful(None)
  }


  private def cityForHandle(cityHandle: Option[String]): Future[Option[City]] = cityHandle match {
    case Some(handle) => locationService.get(Handle(handle)).map(Some(_))
    case None => Future.successful(None)
  }
}
