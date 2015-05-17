package controllers

import java.net.URL

import models.domain.{CompQuery, Handle, Identifiable}
import models.{domain, ui}
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, AnyContent, Call, Result}
import services.{CompService, LocationService, TechService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class AddCompForm(name: String,
                       website: String,
                       city: String,
                       employeeCount:Option[Int],
                       codersCount: Option[Int],
                       femaleCodersCount: Option[Int],
                       note: String,
                       products: Boolean,
                       services: Boolean,
                       internal: Boolean,
                       techs: List[String],
                       joel: List[Int])
case class AddTechToCompForm(techName: String)

trait CompController {
  def addForm: Action[AnyContent]
  def editForm(compId: String): Action[AnyContent]
  def save(compId: Option[String]): Action[AnyContent]
  def all: Action[AnyContent]
  def cityTech(cityHandle: String, techHandle: String): Action[AnyContent]
}

object CompController {
  val addCompForm = Form(
    mapping(
      "compName" -> text,
      "website" -> text,
      "city" -> text,
      "employeeCount" -> optional(number),
      "codersCount" -> optional(number),
      "femaleCodersCount" -> optional(number),
      "note" -> text,
      "products" -> boolean,
      "services" -> boolean,
      "internal" -> boolean,
      "techs" -> list(text),
      "joel" -> list(number)
    )(AddCompForm.apply)(AddCompForm.unapply)
  )

  val addTechToCompForm = Form(
    mapping(
      "techName" -> text
    )(AddTechToCompForm.apply)(AddTechToCompForm.unapply)
  )

  val joelQuestions = List(
    "Do you use source control?",
    "Can you make a build in one step?",
    "Do you make daily builds?",
    "Do you have a bug database?",
    "Do you fix bugs before writing new code?",
    "Do you have an up-to-date schedule?",
    "Do you have a spec?",
    "Do programmers have quiet working conditions?",
    "Do you use the best tools money can buy?",
    "Do you have testers?",
    "Do new candidates write code during their interview?",
    "Do you do hallway usability testing?"
  )
}

class CompControllerImpl(compService: CompService,
                         techService: TechService,
                         locationService: LocationService) extends BaseController with CompController {
  import controllers.CompController._

  override def addForm: Action[AnyContent] = Action.async {
    edit(None, AppLoader.routes.compController.save(None))
  }

  override def editForm(compId: String): Action[AnyContent] = Action.async {
    compService.get(compId).flatMap { comp =>
      edit(Some(comp), AppLoader.routes.compController.save(Some(compId)))
    }
  }

  private def edit(comp: Option[domain.Comp], action: Call): Future[Result] =
    techService.all().map { techs =>
      val ts = techs.map(t => (t.handle.value, comp.exists(_.techs.exists(_.handle == t.handle))))
      Ok(views.html.compEdit(comp.map(ui.Comp.apply), ts, joelQuestions, action))
    }

  override def save(compId: Option[String]) = withForm(addCompForm) { form =>
    locationService.getOrCreateCity(form.city).flatMap { city =>
      compService.upsert(
        domain.Comp(
          id                = compId.getOrElse(Identifiable.empty),
          name              = form.name,
          website           = new URL(form.website),
          city              = city,
          employeeCount     = form.employeeCount,
          codersCount       = form.codersCount,
          femaleCodersCount = form.femaleCodersCount,
          note              = form.note,
          products          = form.products,
          services          = form.services,
          internal          = form.internal,
          techs             = Nil,
          joel              = form.joel.toSet
        ),
        form.techs.map(Handle(_)),
        userId).map { Unit =>
        Redirect(AppLoader.routes.compController.all())
      }
    }
  }

  override def all = cityTech("", "")

  def cityTech(cityHandle: String, techHandle: String) = Action.async { implicit request =>
    val query = request.queryString.get("q").map(q => CompQuery(q.mkString(",")))

    cityForHandle(cityHandle).flatMap { city =>
      techForHandle(techHandle).flatMap { tech =>
        compService.all(city.map(_.handle), tech.map(_.handle)).flatMap { comps =>
          compService.topCities().flatMap { cities =>
            techService.topTechs().map { techs =>
              Ok(views.html.comps(
                headline(city, tech),
                comps.map(ui.Comp(_)),
                cities.take(5).map(ui.City(_)),
                techs.map(ui.Tech(_)),
                city.map(ui.City(_)),
                tech.map(ui.Tech(_)),
                query.map(_.keywords.mkString(","))))
            }
          }
        }.recover {
          case t =>
            Logger.error(s"Cannot get companies for city/tech! [$cityHandle, $techHandle]", t)
            InternalServerError("Oh shit!")
        }
      }.recover {
        case t =>
          Logger.error(s"Cannot get tech for handle! [$techHandle]", t)
          Redirect(AppLoader.routes.compController.cityTech(cityHandle, ""))
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
      " používajúce " + t.handle.value
    }
    "Firmy" + cityHeadline.getOrElse("") + techHeadline.getOrElse("")
  }

  private def techForHandle(techHandle: String): Future[Option[domain.Tech]] =
    if (techHandle.nonEmpty) {
      techService.get(Handle(techHandle)).map(Some(_))
    }
    else
      Future.successful(None)

  private def cityForHandle(cityHandle: String): Future[Option[domain.City]] =
    if (cityHandle.nonEmpty) {
      locationService.get(Handle(cityHandle)).map(Some(_))
    }
    else
      Future.successful(None)
}