package sk.hrstka.controllers.auth.impl

import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import play.api.Application
import play.api.mvc.{Controller, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import sk.hrstka.models.db
import sk.hrstka.models.domain.{Admin, Eminent, UserSpec}
import sk.hrstka.services.AuthService
import sk.hrstka.test.BaseSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.reflect._

class HrstkaAuthConfigSpec extends BaseSpec with ScalaFutures with Results {
  behavior of "idTag"

  it should "be a String" in new TestScope {
    assert(controller.idTag == classTag[String])
  }

  behavior of "sessionTimeoutInSeconds"

  it should "be 1 hour" in new TestScope {
    assert(controller.sessionTimeoutInSeconds == 60*60)
  }

  behavior of "resolveUser"

  it should "find user by email" in new TestScope {
    // Prepare
    when(authService.findByEmail(db.UserSpec.rado.email))
      .thenReturn(Future.successful(Some(UserSpec.rado)))

    // Execute
    assert(controller.resolveUser(db.UserSpec.rado.email).futureValue.contains(UserSpec.rado))

    // Verify
    verify(authService).findByEmail(db.UserSpec.rado.email)
    verifyNoMore()
  }

  behavior of "loginSucceeded"

  it should "redirect to index page" in new TestScope {
    // Execute
    val result = controller.loginSucceeded(FakeRequest())
    assert(status(result) == SEE_OTHER)
    assert(redirectLocation(result).contains("/"))

    // Verify
    verifyNoMore()
  }

  behavior of "logoutSucceeded"

  it should "redirect to index page" in new TestScope {
    // Execute
    val result = controller.logoutSucceeded(FakeRequest())
    assert(status(result) == SEE_OTHER)
    assert(redirectLocation(result).contains("/"))

    // Verify
    verifyNoMore()
  }

  behavior of "authenticationFailed"

  it should "return unauthorized" in new TestScope {
    // Execute
    val result = controller.authenticationFailed(FakeRequest())
    assert(status(result) == UNAUTHORIZED)

    // Verify
    verifyNoMore()
  }

  behavior of "authorizationFailed"

  it should "return forbidden" in new TestScope {
    // Execute
    val result = controller.authorizationFailed(FakeRequest(), UserSpec.johny, Some(Admin))
    assert(status(result) == FORBIDDEN)

    // Verify
    verifyNoMore()
  }

  behavior of "parameterless authorizationFailed"

  it should "return forbidden" in new TestScope {
    intercept[AssertionError] { controller.authorizationFailed(FakeRequest()) }
  }

  behavior of "authorize"

  it should "return true if user is an admin and eminent role is requested" in new TestScope {
    assert(controller.authorize(UserSpec.rado, Eminent).futureValue)
  }

  it should "return false if user is an eminent and admin role is requested" in new TestScope {
    assert(!controller.authorize(UserSpec.johny, Admin).futureValue)
  }

  private class TestScope {
    val authService = mock[AuthService]
    val application = mock[Application]
    val controller = new TestController(authService, application)

    def verifyNoMore(): Unit = {
      verifyNoMoreInteractions(authService)
      verifyNoMoreInteractions(application)
    }
  }
}

private class TestController(protected val authService: AuthService,
                             protected val application: Application) extends Controller with HrstkaAuthConfig
