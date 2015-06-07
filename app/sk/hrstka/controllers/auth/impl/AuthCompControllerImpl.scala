package sk.hrstka.controllers.auth.impl

import java.net.URL

import com.google.inject._
import jp.t2v.lab.play2.auth.AuthElement
import jp.t2v.lab.play2.stackc.RequestWithAttributes
import play.api.Application
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesApi
import play.api.mvc._
import sk.hrstka.controllers.auth.{AddCompForm, AuthCompController}
import sk.hrstka.controllers.impl.{BaseController, MainModelProvider}
import sk.hrstka.models.domain.{Comp, Eminent, Handle, Identifiable}
import sk.hrstka.models.ui.CompFactory
import sk.hrstka.services.{AuthService, CompService, LocationService, TechService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
final class AuthCompControllerImpl @Inject() (compService: CompService,
                                              protected val authService: AuthService,
                                              protected val techService: TechService,
                                              protected val locationService: LocationService,
                                              protected val application: Application,
                                              val messagesApi: MessagesApi)
  extends BaseController with AuthCompController with MainModelProvider with HrstkaAuthConfig with AuthElement {
  import AuthCompControllerImpl._

  override def addForm(): Action[AnyContent] = AsyncStack(AuthorityKey -> Eminent) { implicit request =>
    edit(None, sk.hrstka.controllers.auth.routes.AuthCompController.save(None))
  }

  override def editForm(compId: String): Action[AnyContent] = AsyncStack(AuthorityKey -> Eminent) { implicit request =>
    compService.get(compId).flatMap { comp =>
      edit(Some(comp), sk.hrstka.controllers.auth.routes.AuthCompController.save(Some(compId)))
    }
  }

  override def save(compId: Option[String]): Action[AnyContent] = AsyncStack(AuthorityKey -> Eminent) { implicit request =>
    withForm(addCompForm) { form =>
      locationService.getOrCreateCity(form.city).flatMap { city =>
        compService.upsert(
          Comp(
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
            techRatings = Set.empty,
            joel = form.joel.toSet
          ),
          form.techs.map(Handle.apply).toSet,
          loggedIn.email
        ).map { _ =>
          Redirect(sk.hrstka.controllers.routes.CompController.all())
        }
      }
    }
  }

  private def edit[A](comp: Option[Comp], action: Call)(implicit request: RequestWithAttributes[A]): Future[Result] =
    for {
      techRatings <- techService.allRatings()
      companyTechnologies = techRatings.map { techRating =>
        techRating.tech.handle.value -> comp.exists(_.techRatings.exists(_.tech.handle == techRating.tech.handle))
      }
      result <- withMainModel(None, None, Some(loggedIn)) { implicit mainModel =>
        Ok(sk.hrstka.views.html.compEdit(comp.map(CompFactory.apply), companyTechnologies, joelQuestions, action))
      }
    } yield result
}

object AuthCompControllerImpl {
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