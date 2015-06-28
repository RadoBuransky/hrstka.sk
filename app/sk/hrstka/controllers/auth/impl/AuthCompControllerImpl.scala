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
import sk.hrstka.models.domain._
import sk.hrstka.models.ui.{CompFactory, Markdown}
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

  override def editForm(businessNumber: String): Action[AnyContent] = AsyncStack(AuthorityKey -> Eminent) { implicit request =>
    compService.get(BusinessNumber(businessNumber)).flatMap { comp =>
      edit(Some(comp), sk.hrstka.controllers.auth.routes.AuthCompController.save(Some(comp.id.value)))
    }
  }

  override def save(compId: Option[String]): Action[AnyContent] = AsyncStack(AuthorityKey -> Eminent) { implicit request =>
    withForm(addCompForm) { form =>
      locationService.getOrCreateCity(form.city).flatMap { city =>
        compService.upsert(
          Comp(
            id = compId.map(Id).getOrElse(Identifiable.empty),
            name = form.name,
            website = new URL(form.website),
            city = city,
            businessNumber = BusinessNumber(form.businessNumber),
            employeeCount = form.employeeCount,
            codersCount = form.codersCount,
            femaleCodersCount = form.femaleCodersCount,
            note = form.note,
            products = form.products,
            services = form.services,
            internal = form.internal,
            techRatings = Seq.empty,
            joel = form.joel.toSet,
            govBiz = form.govBiz
          ),
          form.techs.map(Handle.apply).toSet,
          loggedIn.id
        ).map { businessNumber =>
          Redirect(sk.hrstka.controllers.routes.CompController.get(businessNumber.value))
        }
      }
    }
  }

  override def voteUp(businessNumber: String): Action[AnyContent] = AsyncStack(AuthorityKey -> Eminent) { implicit request =>
    vote(businessNumber, compService.voteUp(BusinessNumber(businessNumber), loggedIn.id))
  }

  override def voteDown(businessNumber: String): Action[AnyContent] = AsyncStack(AuthorityKey -> Eminent) { implicit request =>
    vote(businessNumber, compService.voteDown(BusinessNumber(businessNumber), loggedIn.id))
  }

  private def vote[A](businessNumber: String, action: Future[Unit])(implicit request: RequestWithAttributes[A]) =
    action.map { Unit =>
      Redirect(sk.hrstka.controllers.routes.CompController.get(businessNumber))
    }

  private def edit[A](comp: Option[Comp], action: Call)(implicit request: RequestWithAttributes[A]): Future[Result] =
    for {
      techRatings <- techService.allRatings()
      companyTechnologies = techRatings.map { techRating =>
        techRating.tech.handle.value -> comp.exists(_.techRatings.exists(_.tech.handle == techRating.tech.handle))
      }
      result <- withMainModel(None, None, Some(loggedIn)) { implicit mainModel =>
        Ok(sk.hrstka.views.html.auth.compEdit(comp.map(c => CompFactory.apply(c, Markdown(c.note))), companyTechnologies, CompFactory.joelQuestions, action))
      }
    } yield result
}

object AuthCompControllerImpl {
  val addCompForm = Form(
    mapping(
      "compName" -> text,
      "website" -> text,
      "city" -> text,
      "businessNumber" -> text,
      "employeeCount" -> optional(number),
      "codersCount" -> optional(number),
      "femaleCodersCount" -> optional(number),
      "govBiz" -> optional(bigDecimal),
      "note" -> text,
      "products" -> boolean,
      "services" -> boolean,
      "internal" -> boolean,
      "techs" -> list(text),
      "joel" -> list(number)
    )(AddCompForm.apply)(AddCompForm.unapply)
  )
}