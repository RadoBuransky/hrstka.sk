package controllers.auth

import com.google.inject.{Inject, Singleton, ImplementedBy}
import controllers.{BaseController, MainModelProvider}
import jp.t2v.lab.play2.auth.AuthElement
import jp.t2v.lab.play2.stackc.RequestWithAttributes
import models.domain.Admin
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

  override def all: Action[AnyContent] = AsyncStack(AuthorityKey -> Admin) { implicit request =>
    val serviceResult = for {
      techs <- techService.all()
      userVotes <- techService.votesFor(userId)
    } yield (techs, userVotes)

    serviceResult.flatMap {
      case (techs, userVotes) =>
        withMainModel(None, None, Some(loggedIn)) { implicit mainModel =>
          Ok(views.html.techs(None, techs.map { tech =>
            Tech(tech, userVotes.find(_.techId == tech.id).map(_.value))
          }))
        }
    }
  }

  override def add: Action[AnyContent] = AsyncStack(AuthorityKey -> Admin) { implicit request =>
    withForm(addTechForm) { form =>
      techService.insert(form.techName, userId).map { Unit =>
        Redirect(controllers.auth.routes.AuthTechController.all())
      }
    }
  }

  override def voteUp(id: String) = AsyncStack(AuthorityKey -> Admin) { implicit request =>
    vote(techService.voteUp(id, userId))
  }
  override def voteDown(id: String) = AsyncStack(AuthorityKey -> Admin) { implicit request =>
    vote(techService.voteDown(id, userId))
  }

  private def vote[A](action: Future[Unit])(implicit request: RequestWithAttributes[A]) =
    action.map { Unit =>
      Redirect(controllers.auth.routes.AuthTechController.all())
    }
}