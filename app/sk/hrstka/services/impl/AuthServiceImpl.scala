package sk.hrstka.services.impl

import com.github.t3hnar.bcrypt._
import com.google.inject.{Inject, Singleton}
import sk.hrstka.common.Logging
import sk.hrstka.models.domain._
import sk.hrstka.models.{db, domain}
import sk.hrstka.repositories.UserRepository
import sk.hrstka.services.AuthService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
final class AuthServiceImpl @Inject() (userRepository: UserRepository) extends AuthService with Logging {
  def createUser(email: Email, password: String): Future[Unit] = {
    userRepository.insert(db.User(db.Identifiable.empty, email.value, encrypt(password), Eminent.name)).map { lastError =>
      logger.info(s"User created. [$email]")
    }
  }

  def findByEmail(email: Email): Future[Option[domain.User]] = {
    logger.debug(s"Find by email. [$email]")
    userRepository.findByEmail(email.value).map(_.map(UserFactory(_)))
  }

  def authenticate(email: Email, password: String): Future[Option[domain.User]] = {
    val result = userRepository.findByEmail(email.value).map {
      case Some(user) if check(password, user.encryptedPassword) => {
        logger.debug(s"User authenticated. [$email]")
        Some(domain.User(Identifiable.fromBSON(user._id), email, Role(user.role)))
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