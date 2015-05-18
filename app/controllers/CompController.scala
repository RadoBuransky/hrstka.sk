package controllers

import java.net.URL

import models.domain.{Handle, Identifiable}
import models.{domain, ui}
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
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
                         protected val techService: TechService,
                         protected val locationService: LocationService) extends BaseController with CompController with MainModelProvider {
  import controllers.CompController._

  override def addForm: Action[AnyContent] = Action.async { implicit request =>
    edit(None, AppLoader.routes.compController.save(None))
  }

  override def editForm(compId: String): Action[AnyContent] = Action.async { implicit request =>
    compService.get(compId).flatMap { comp =>
      edit(Some(comp), AppLoader.routes.compController.save(Some(compId)))
    }
  }

  private def edit[A](comp: Option[domain.Comp], action: Call)(implicit request: Request[A]): Future[Result] =
    techService.all().flatMap { techs =>
      val ts = techs.map(t => (t.handle.value, comp.exists(_.techs.exists(_.handle == t.handle))))
      withMainModel() { implicit mainModel =>
        Ok(views.html.compEdit(comp.map(ui.Comp.apply), ts, joelQuestions, action))
      }
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
  override def cityTech(cityHandle: String, techHandle: String) =
    cityTechAction(Option(cityHandle).filter(_.trim.nonEmpty), Option(techHandle).filter(_.trim.nonEmpty))

  private def cityTechAction(cityHandle: Option[String], techHandle: Option[String]) = Action.async { implicit request =>
    cityForHandle(cityHandle).flatMap { city =>
      techForHandle(techHandle).flatMap { tech =>
        compService.all(city.map(_.handle), tech.map(_.handle)).flatMap { comps =>
            withMainModel(cityHandle, techHandle) { implicit mainModel =>
              Ok(views.html.index(
                headline(city, tech),
                comps.map(ui.Comp(_))))
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