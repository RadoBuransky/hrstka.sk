package sk.hrstka.services.impl

import com.github.t3hnar.bcrypt._
import com.google.inject.{Inject, Singleton}
import sk.hrstka.common.Logging
import sk.hrstka.models.db.{Identifiable, User}
import sk.hrstka.models.domain
import sk.hrstka.models.domain.{Eminent, Role, UserFactory}
import sk.hrstka.repositories.UserRepository
import sk.hrstka.services.AuthService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
final class AuthServiceImpl @Inject() (userRepository: UserRepository) extends AuthService with Logging {
  def createUser(email: String, password: String): Future[Unit] = {
    userRepository.insert(User(Identifiable.empty, email, encrypt(password), Eminent.name)).map { lastError =>
      logger.info(s"User created. [$email]")
    }
  }

  def findByEmail(email: String): Future[Option[domain.User]] = {
    logger.debug(s"Find by email. [$email]")
    userRepository.findByEmail(email).map(_.map(UserFactory(_)))
  }

  def authenticate(email: String, password: String): Future[Option[domain.User]] = {
    val result = userRepository.findByEmail(email).map {
      case Some(user) if check(password, user.encryptedPassword) => {
        logger.debug(s"User authenticated. [$email]")
        Some(domain.User(user._id.stringify, email, Role(user.role)))
      }
      case Some(user) => {
        logger.debug(s"Invalid password. [$email]")
        None
      }
      case _ => {
        logger.debug(s"User not found. [$email]")
        None
      }
    }

    result
  }

  private def encrypt(clear: String): String = clear.bcrypt
  private def check(candidate: String, encryptedPassword: String): Boolean = candidate.isBcrypted(encryptedPassword)
}