package services.impl

import com.github.t3hnar.bcrypt._
import models.db
import models.db.Identifiable
import models.domain.{Eminent, User}
import play.api.Logger
import repositories.UserRepository
import services.AuthService

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class AuthServiceImpl(userRepository: UserRepository) extends AuthService {
  def createUser(user: User, password: String): Future[User] = {
    userRepository.insert(db.User(Identifiable.empty, user.email, encrypt(password), Eminent.name)).map { lastError =>
      Logger.info(s"User created. [${user.email}]")
      user
    }
  }
  def findByEmail(email: String): Future[Option[User]] = userRepository.findByEmail(email).map(_.map(User(_)))
  def authenticate(email: String, password: String): Future[Option[User]] = {
    val result = userRepository.findByEmail(email).map {
      case Some(user) if check(password, user.encryptedPassword) => {
        Logger.debug(s"User authenticated. [$email]")
        Some(User(email, Eminent))
      }
      case Some(user) => {
        Logger.debug(s"Invalid password. [$email]")
        None
      }
      case _ => {
        Logger.debug(s"User not found. [$email]")
        None
      }
    }

    result
  }

  private def encrypt(clear: String): String = clear.bcrypt
  private def check(candidate: String, encryptedPassword: String): Boolean = candidate.isBcrypted(encryptedPassword)
}
