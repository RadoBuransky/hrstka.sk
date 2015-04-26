package auth

import play.api.Logger

import scala.concurrent.Future

trait Account {
  def id: String
  def email: String
  def role: Role
}

object Account {
  def findById(id: String): Future[Option[Account]] = Future.successful(Some(AccountImpl(id, Eminent)))
  def authenticate(email: String, password: String): Option[Account] = {
    val result = email match {
      case "radoburansky@gmail.com" => Some(AccountImpl(email, Eminent))
      case _ => None
    }
    Logger.debug(s"Account.authenticate [$email, ${result.isDefined}]")
    result
  }
}

private case class AccountImpl(email: String, role: Role) extends Account {
  def id = email
}