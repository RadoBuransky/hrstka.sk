package services

import com.google.inject.ImplementedBy
import models.domain.User
import services.impl.AuthServiceImpl

import scala.concurrent.Future

@ImplementedBy(classOf[AuthServiceImpl])
trait AuthService {
  def createUser(email: String, password: String): Future[Unit]
  def findByEmail(email: String): Future[Option[User]]
  def authenticate(email: String, password: String): Future[Option[User]]
}