package controllers.auth

import java.util.Base64

import jp.t2v.lab.play2.auth._
import models.domain.{Admin, Eminent, Role}
import play.api.mvc.{Controller, RequestHeader, Result}
import services.AuthService

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect._

trait HrstkaAuthConfig extends AuthConfig {
  self: Controller =>
  
  protected def authService: AuthService
  
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
  val sessionTimeoutInSeconds: Int = HrstkaAuthConfig.sessionTimeoutInSeconds

  /**
   * A function that returns a `User` object from an `Id`.
   * You can alter the procedure to suit your application.
   */
  def resolveUser(id: Id)(implicit ctx: ExecutionContext): Future[Option[User]] = authService.findByEmail(id)

  /**
   * Where to redirect the user after a successful login.
   */
  def loginSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] = {
    Future.successful(Redirect(controllers.routes.CompController.all()))
  }

  /**
   * Where to redirect the user after logging out
   */
  def logoutSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] = {
    Future.successful(Redirect(controllers.routes.CompController.all()))
  }

  /**
   * If the user is not logged in and tries to access a protected resource then redirct them as follows:
   */
  def authenticationFailed(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] = {
    Future.successful(Unauthorized)
  }

  /**
   * If authorization failed (usually incorrect password) redirect the user as follows:
   */
  override def authorizationFailed(request: RequestHeader, user: User, authority: Option[Authority])(implicit context: ExecutionContext): Future[Result] = {
    Future.successful(Forbidden)
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
  override lazy val tokenAccessor = new Base64CookieTokenAccessor
}

object HrstkaAuthConfig {
  val sessionTimeoutInSeconds: Int = 3600
}

/**
 * Encode cookie to base 64 due to some validation issues.
 */
class Base64CookieTokenAccessor extends CookieTokenAccessor(
  cookieSecureOption = play.api.Play.isProd(play.api.Play.current),
  cookieMaxAge       = Some(HrstkaAuthConfig.sessionTimeoutInSeconds)) {
  override protected def verifyHmac(token: SignedToken): Option[AuthenticityToken] =
    super.verifyHmac(new String(Base64.getDecoder.decode(token)))

  override protected def sign(token: AuthenticityToken): SignedToken =
    Base64.getEncoder.encodeToString(super.sign(token).getBytes)
}