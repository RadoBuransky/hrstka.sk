package services.impl

import models.domain.{Account, Eminent}
import play.api.Logger
import services.AuthService
import com.github.t3hnar.bcrypt._

import scala.concurrent.Future

class AuthServiceImpl extends AuthService {
  def findById(id: String): Future[Option[Account]] = Future.successful(Some(Account(id, Eminent)))
  def authenticate(email: String, password: String): Option[Account] = {
    val result = email match {
      case "radoburansky@gmail.com" => Some(Account(email, Eminent))
      case _ => None
    }
    Logger.debug(s"Account.authenticate [$email, ${result.isDefined}]")
    result
  }

  private def encrypt(clear: String): String = clear.bcrypt
  private def check(candidate: String, encryptedPassword: String): Boolean = candidate.isBcrypted(encryptedPassword)
}
