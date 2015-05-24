package controllers.auth

import java.net.URL

import controllers.MainModelProvider
import jp.t2v.lab.play2.auth.AuthElement
import jp.t2v.lab.play2.stackc.RequestWithAttributes
import models.domain.{Admin, Handle, Identifiable}
import models.{domain, ui}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import services.{AuthService, CompService, LocationService, TechService}

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

trait AuthCompController {
  def addForm: Action[AnyContent]
  def editForm(compId: String): Action[AnyContent]
  def save(compId: Option[String]): Action[AnyContent]
}

object AuthCompController {
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

class AuthCompControllerImpl(compService: CompService,
                             authService: AuthService,
                             protected val techService: TechService,
                             protected val locationService: LocationService)
  extends AuthConfigImpl(authService) with AuthCompController with MainModelProvider with AuthElement {
  import AuthCompController._

  override def addForm: Action[AnyContent] = AsyncStack(AuthorityKey -> Admin) { implicit request =>
    edit(None, AppLoader.routes.authCompController.save(None))
  }

  override def editForm(compId: String): Action[AnyContent] = AsyncStack(AuthorityKey -> Admin) { implicit request =>
    compService.get(compId).flatMap { comp =>
      edit(Some(comp), AppLoader.routes.authCompController.save(Some(compId)))
    }
  }

  override def save(compId: Option[String]): Action[AnyContent] = AsyncStack(AuthorityKey -> Admin) { implicit request =>
    withForm(addCompForm) { form =>
      locationService.getOrCreateCity(form.city).flatMap { city =>
        compService.upsert(
          domain.Comp(
            id = compId.getOrElse(Identifiable.empty),
            name = form.name,
            website = new URL(form.website),
            city = city,
            employeeCount = form.employeeCount,
            codersCount = form.codersCount,
            femaleCodersCount = form.femaleCodersCount,
            note = form.note,
            products = form.products,
            services = form.services,
            internal = form.internal,
            techs = Nil,
            joel = form.joel.toSet
          ),
          form.techs.map(Handle(_)),
          userId).map { Unit =>
          Redirect(AppLoader.routes.compController.all())
        }
      }
    }
  }

  private def edit[A](comp: Option[domain.Comp], action: Call)(implicit request: RequestWithAttributes[A]): Future[Result] =
    techService.all().flatMap { techs =>
      val ts = techs.map(t => (t.handle.value, comp.exists(_.techs.exists(_.handle == t.handle))))
      withMainModel(None, None, Some(loggedIn)) { implicit mainModel =>
        Ok(views.html.compEdit(comp.map(ui.Comp.apply), ts, joelQuestions, action))
      }
    }
}