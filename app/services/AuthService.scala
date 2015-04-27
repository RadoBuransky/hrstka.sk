package services

import models.domain.User

import scala.concurrent.Future

trait AuthService {
  def createUser(user: User, password: String): Future[User]
  def findByEmail(email: String): Future[Option[User]]
  def authenticate(email: String, password: String): Future[Option[User]]
}