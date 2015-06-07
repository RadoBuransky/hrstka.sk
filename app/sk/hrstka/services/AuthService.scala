package sk.hrstka.services

import com.google.inject.ImplementedBy
import sk.hrstka.models.domain.{Email, User}
import sk.hrstka.services.impl.AuthServiceImpl

import scala.concurrent.Future

/**
 * Authentication service.
 */
@ImplementedBy(classOf[AuthServiceImpl])
trait AuthService {
  /**
   * Creates new user.
   *
   * @param email User email, must be unique.
   * @param password User plain password.
   * @return Nothing.
   */
  def createUser(email: Email, password: String): Future[Unit]

  /**
   * Finds an user by email.
   *
   * @param email User email.
   * @return User if found.
   */
  def findByEmail(email: Email): Future[Option[User]]

  /**
   * Tries to find an user for the email and checks if password matches.
   *
   * @param email User email.
   * @param password User password.
   * @return User if found and if password is correct.
   */
  def authenticate(email: Email, password: String): Future[Option[User]]
}