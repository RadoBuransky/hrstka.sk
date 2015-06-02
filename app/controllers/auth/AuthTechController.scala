package controllers.auth

import java.net.URL

import com.google.inject.{ImplementedBy, Inject, Singleton}
import controllers.{BaseController, MainModelProvider}
import jp.t2v.lab.play2.auth.AuthElement
import jp.t2v.lab.play2.stackc.RequestWithAttributes
import models.domain
import models.domain.{Handle, Identifiable, Other}
import models.ui.Tech
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent}
import services.{AuthService, LocationService, TechService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class AddTechForm(techName: String)

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
      "techName" -> text
    )(AddTechForm.apply)(AddTechForm.unapply)
  )
}

@Singleton
class AuthTechControllerImpl @Inject() (protected val authService: AuthService,
                                        protected val locationService: LocationService,
                                        protected val techService: TechService,
                                        val messagesApi: MessagesApi)
  extends BaseController with AuthTechController with MainModelProvider with HrstkaAuthConfig with AuthElement {
  import AuthTechController._

  override def all: Action[AnyContent] = AsyncStack(AuthorityKey -> domain.Admin) { implicit request =>
    val serviceResult = for {
      techRatings <- techService.allRatings()
      userVotes <- techService.votesFor(userId)
    } yield (techRatings, userVotes)

    serviceResult.flatMap {
      case (techRatings, userVotes) =>
        withMainModel(None, None, Some(loggedIn)) { implicit mainModel =>
          Ok(views.html.techs(None, techRatings.map { techRating =>
            // TODO: ui.TechRating ...
            Tech(techRating.tech)
          }))
        }
    }
  }

  override def add: Action[AnyContent] = AsyncStack(AuthorityKey -> domain.Admin) { implicit request =>
    withForm(addTechForm) { form =>
      // TODO: Get proper values ...
      techService.upsert(domain.Tech(
        id        = Identifiable.empty,
        handle    = Handle.fromHumanName(form.techName),
        category  = Other,
        name      = form.techName,
        website   = new URL("http://www.google.com/")
      )).map { techId =>
        Redirect(controllers.auth.routes.AuthTechController.all())
      }
    }
  }

  override def voteUp(id: String) = AsyncStack(AuthorityKey -> domain.Admin) { implicit request =>
    vote(techService.voteUp(id, userId))
  }
  override def voteDown(id: String) = AsyncStack(AuthorityKey -> domain.Admin) { implicit request =>
    vote(techService.voteDown(id, userId))
  }

  private def vote[A](action: Future[Unit])(implicit request: RequestWithAttributes[A]) =
    action.map { Unit =>
      Redirect(controllers.auth.routes.AuthTechController.all())
    }
}