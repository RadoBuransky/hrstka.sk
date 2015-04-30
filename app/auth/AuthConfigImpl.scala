package auth

import AppLoader.routes
import controllers.BaseController
import jp.t2v.lab.play2.auth.{AuthConfig, CookieTokenAccessor}
import models.domain.{Admin, Eminent, Role}
import play.api.Logger
import play.api.mvc.{RequestHeader, Result}
import services.AuthService

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect._

class AuthConfigImpl(authService: AuthService) extends BaseController with AuthConfig {

  /**
   * A type that is used to identify a user.
   * `String`, `Int`, `Long` and so on.
   */
  type Id = String

  /**
   * A type that represents a user in your application.
   * `User`, `Account` and so on.
   */
  type User = models.domain.User

  /**
   * A type that is defined by every action for authorization.
   * This sample uses the following trait:
   *
   * sealed trait Role
   * case object Administrator extends Role
   * case object NormalUser extends Role
   */
  type Authority = Role

  /**
   * A `ClassTag` is used to retrieve an id from the Cache API.
   * Use something like this:
   */
  val idTag: ClassTag[Id] = classTag[Id]

  /**
   * The session timeout in seconds
   */
  val sessionTimeoutInSeconds: Int = 3600

  /**
   * A function that returns a `User` object from an `Id`.
   * You can alter the procedure to suit your application.
   */
  def resolveUser(id: Id)(implicit ctx: ExecutionContext): Future[Option[User]] = authService.findByEmail(id)

  /**
   * Where to redirect the user after a successful login.
   */
  def loginSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] = {
    Logger.debug("loginSucceeded")
    Future.successful(Redirect(routes.compController.all()))
  }

  /**
   * Where to redirect the user after logging out
   */
  def logoutSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] = {
    Logger.debug("logoutSucceeded")
    Future.successful(Redirect(routes.compController.all()))
  }

  /**
   * If the user is not logged in and tries to access a protected resource then redirct them as follows:
   */
  def authenticationFailed(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] = {
    Logger.debug("authenticationFailed")
    Future.successful(Forbidden("authentication failed"))
  }

  /**
   * If authorization failed (usually incorrect password) redirect the user as follows:
   */
  override def authorizationFailed(request: RequestHeader, user: User, authority: Option[Authority])(implicit context: ExecutionContext): Future[Result] = {
    Logger.debug("authorizationFailed")
    Future.successful(Forbidden("authorization failed"))
  }

  /**
   * This method is kept for compatibility.
   * It will be removed in a future version
   * Override `authorizationFailed(RequestHeader, User, Option[Authority])` instead of this
   */
  def authorizationFailed(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] = throw new AssertionError

  /**
   * A function that determines what `Authority` a user has.
   * You should alter this procedure to suit your application.
   */
  def authorize(user: User, authority: Authority)(implicit ctx: ExecutionContext): Future[Boolean] = Future.successful {
    Logger.debug(s"Authorize [${user.email}, ${user.role.name}, $authority]")
    (user.role, authority) match {
      case (Admin, _)         => true
      case (Eminent, Admin)   => false
      case (Eminent, Eminent) => true
      case _                  => false
    }
  }

  /**
   * (Optional)
   * You can custom SessionID Token handler.
   * Default implemntation use Cookie.
   */
  override lazy val tokenAccessor = new CookieTokenAccessor(
    /*
     * Whether use the secure option or not use it in the cookie.
     * However default is false, I strongly recommend using true in a production.
     */
    cookieSecureOption = play.api.Play.isProd(play.api.Play.current),
    cookieMaxAge       = Some(sessionTimeoutInSeconds)
  )
}
