package sk.hrstka.controllers.auth.impl

import jp.t2v.lab.play2.auth.AuthElement
import jp.t2v.lab.play2.stackc.RequestWithAttributes
import play.api.Mode
import play.api.mvc.{Controller, Result}
import sk.hrstka.models.domain.{Email, Eminent, Identifiable, User}

import scala.concurrent.Future

/**
 * Use hardcoded eminent role for convenient development.
 */
private[controllers] trait HrstkaAuthElement extends AuthElement {
  self: Controller with HrstkaAuthConfig =>

  override def proceed[A](req: RequestWithAttributes[A])(f: RequestWithAttributes[A] => Future[Result]): Future[Result] =
    application.mode match {
      case Mode.Dev => f(req.set(AuthorityKey, Eminent))
      case _ => super.proceed(req)(f)
    }

  override implicit def loggedIn(implicit req: RequestWithAttributes[_]): User =
    application.mode match {
      case Mode.Dev => HrstkaAuthElement.devUser
      case _ => super.loggedIn(req)
    }
}

private[controllers] object HrstkaAuthElement {
  val devUser = User(
    id = Identifiable.empty,
    email = Email("dev@hrstka.sk"),
    role = Eminent
  )
}