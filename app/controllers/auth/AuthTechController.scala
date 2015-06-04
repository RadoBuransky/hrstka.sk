package controllers.auth

import java.net.URL

import com.google.inject.{ImplementedBy, Inject, Singleton}
import controllers.{BaseController, MainModelProvider}
import jp.t2v.lab.play2.auth.AuthElement
import jp.t2v.lab.play2.stackc.RequestWithAttributes
import models.{domain, ui}
import play.api.Application
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent}
import sk.hrstka.services.{AuthService, LocationService, TechService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class AddTechForm(categoryHandle: String,
                       name: String,
                       website: String)

@ImplementedBy(classOf[AuthTechControllerImpl])
trait AuthTechController {
  def all: Action[AnyContent]
  def add: Action[AnyContent]
  def voteUp(id: String): Action[AnyContent]
  def voteDown(id: String): Action[AnyContent]
}

object AuthTechController {
  val addTechForm = Form(
    mapping(
      "categoryHandle" -> text,
      "name" -> text,
      "website" -> text
    )(AddTechForm.apply)(AddTechForm.unapply)
  )
}

@Singleton
class AuthTechControllerImpl @Inject() (protected val authService: AuthService,
                                        protected val locationService: LocationService,
                                        protected val techService: TechService,
                                        protected val application: Application,
                                        val messagesApi: MessagesApi)
  extends BaseController with AuthTechController with MainModelProvider with HrstkaAuthConfig with AuthElement {
  import AuthTechController._

  override def all: Action[AnyContent] = AsyncStack(AuthorityKey -> domain.Eminent) { implicit request =>
    val serviceResult = for {
      allRatings <- techService.allRatings()
      allCategories <- techService.allCategories()
      userVotes <- techService.votesFor(loggedIn.id)
    } yield (allRatings, allCategories, userVotes)

    serviceResult.flatMap {
      case (techRatings, allCategories, userVotes) =>
        withMainModel(None, None, Some(loggedIn)) { implicit mainModel =>
          Ok(views.html.techs(
            None,
            techRatings.map(ui.TechRatingFactory.apply),
            userVotes.map(uv => uv.techId -> uv.value).toMap,
            allCategories.map(ui.TechCategoryFactory.apply)))
        }
    }
  }

  override def add: Action[AnyContent] = AsyncStack(AuthorityKey -> domain.Eminent) { implicit request =>
    withForm(addTechForm) { form =>
      techService.upsert(domain.Tech(
        id        = domain.Identifiable.empty,
        handle    = domain.HandleFactory.fromHumanName(form.name),
        category  = domain.TechCategory(form.categoryHandle),
        name      = form.name,
        website   = new URL(form.website)
      )).map { techId =>
        Redirect(controllers.auth.routes.AuthTechController.all())
      }
    }
  }

  override def voteUp(id: String) = AsyncStack(AuthorityKey -> domain.Eminent) { implicit request =>
    vote(techService.voteUp(id, loggedIn.id))
  }

  override def voteDown(id: String) = AsyncStack(AuthorityKey -> domain.Eminent) { implicit request =>
    vote(techService.voteDown(id, loggedIn.id))
  }

  private def vote[A](action: Future[Unit])(implicit request: RequestWithAttributes[A]) =
    action.map { Unit =>
      Redirect(controllers.auth.routes.AuthTechController.all())
    }
}