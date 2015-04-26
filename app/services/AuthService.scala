package services

import models.domain.Account

import scala.concurrent.Future

trait AuthService {
  def findById(id: String): Future[Option[Account]]
  def authenticate(email: String, password: String): Option[Account]
}