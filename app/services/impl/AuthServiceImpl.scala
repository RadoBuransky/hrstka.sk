package services.impl

import com.github.t3hnar.bcrypt._
import com.google.inject.{Inject, Singleton}
import models.db
import models.db.Identifiable
import models.domain.{Eminent, Role, User}
import play.api.Logger
import repositories.UserRepository
import services.AuthService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
final class AuthServiceImpl @Inject() (userRepository: UserRepository) extends AuthService {
  def createUser(email: String, password: String): Future[Unit] = {
    userRepository.insert(db.User(Identifiable.empty, email, encrypt(password), Eminent.name)).map { lastError =>
      Logger.info(s"User created. [$email]")
    }
  }

  def findByEmail(email: String): Future[Option[User]] = {
    Logger.debug(s"Find by email. [$email]")
    userRepository.findByEmail(email).map(_.map(User(_)))
  }

  def authenticate(email: String, password: String): Future[Option[User]] = {
    val result = userRepository.findByEmail(email).map {
      case Some(user) if check(password, user.encryptedPassword) => {
        Logger.debug(s"User authenticated. [$email]")
        Some(User(user._id.stringify, email, Role(user.role)))
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