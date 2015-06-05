package sk.hrstka.controllers

import com.google.inject._
import jp.t2v.lab.play2.auth.{AuthConfig, LoginLogout}
import play.api.mvc.{Action, AnyContent, Controller}
import sk.hrstka.controllers.impl.AuthControllerImpl

case class LoginForm(email: String, password: String)
case class RegisterForm(email: String, password: String, passwordAgain: String)

@ImplementedBy(classOf[AuthControllerImpl])
trait AuthController extends Controller with LoginLogout with AuthConfig {
  def login: Action[AnyContent]
  def logout: Action[AnyContent]
  def authenticate: Action[AnyContent]
  def register: Action[AnyContent]
  def registerView: Action[AnyContent]
}

