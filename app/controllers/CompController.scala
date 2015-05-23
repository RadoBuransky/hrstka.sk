package controllers

import auth.AuthConfigImpl
import jp.t2v.lab.play2.auth.OptionalAuthElement
import models.domain.Handle
import models.{domain, ui}
import play.api.Logger
import play.api.mvc._
import services.{AuthService, CompService, LocationService, TechService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait CompController {
  def get(compId: String): Action[AnyContent]
  def women: Action[AnyContent]
  def all: Action[AnyContent]
  def cityTech(cityHandle: String, techHandle: String): Action[AnyContent]
}

class CompControllerImpl(compService: CompService,
                         authService: AuthService,
                         protected val techService: TechService,
                         protected val locationService: LocationService)
  extends AuthConfigImpl(authService) with CompController with MainModelProvider with OptionalAuthElement {

  def get(compId: String): Action[AnyContent] = AsyncStack { implicit request =>
    compService.get(compId).flatMap { comp =>
      withMainModel(None, None, loggedIn.map(_.role)) { implicit mainModel =>
        Ok(views.html.comp(ui.Comp(comp)))
      }
    }
  }

  override def women: Action[AnyContent] = AsyncStack { implicit request =>
    compService.topWomen().flatMap { topWomen =>
      withMainModel(None, None, loggedIn.map(_.role)) { implicit mainModel =>
        Ok(views.html.women(topWomen.map(ui.Comp(_))))
      }
    }
  }

  override def all = cityTech("", "")
  override def cityTech(cityHandle: String, techHandle: String) =
    cityTechAction(Option(cityHandle).filter(_.trim.nonEmpty), Option(techHandle).filter(_.trim.nonEmpty))

  private def cityTechAction(cityHandle: Option[String], techHandle: Option[String]) = AsyncStack { implicit request =>
    cityForHandle(cityHandle).flatMap { city =>
      techForHandle(techHandle).flatMap { tech =>
        compService.all(city.map(_.handle), tech.map(_.handle)).flatMap { comps =>
            withMainModel(cityHandle, techHandle, loggedIn.map(_.role)) { implicit mainModel =>
              Ok(views.html.index(
                headline(city, tech),
                comps.sortBy(_.rank). map(ui.Comp(_))))
          }
        }.recover {
          case t =>
            Logger.error(s"Cannot get companies for city/tech! [$cityHandle, $techHandle]", t)
            InternalServerError("Oh shit!")
        }
      }.recover {
        case t =>
          Logger.error(s"Cannot get tech for handle! [$techHandle]", t)
          Redirect(AppLoader.routes.compController.cityTech(cityHandle.getOrElse(""), ""))
      }
    }.recover {
      case t =>
        Logger.error(s"Cannot get city for handle! [$cityHandle]", t)
        Redirect(AppLoader.routes.compController.all())
    }
  }

  private def headline(city: Option[domain.City], tech: Option[domain.Tech]): String = {
    val cityHeadline = city.map { c =>
      " v meste " + c.sk
    }
    val techHeadline = tech.map { t =>
      " používajúce " + t.handle.value.capitalize
    }
    if (city.isEmpty && tech.isEmpty)
      "Firmy na Slovensku kde sa programuje"
    else
      "Firmy" + cityHeadline.getOrElse("") + techHeadline.getOrElse("")
  }

  private def techForHandle(techHandle: Option[String]): Future[Option[domain.Tech]] = techHandle match {
    case Some(handle) => techService.get(Handle(handle)).map(Some(_))
    case None => Future.successful(None)
  }


  private def cityForHandle(cityHandle: Option[String]): Future[Option[domain.City]] = cityHandle match {
    case Some(handle) => locationService.get(Handle(handle)).map(Some(_))
    case None => Future.successful(None)
  }
}