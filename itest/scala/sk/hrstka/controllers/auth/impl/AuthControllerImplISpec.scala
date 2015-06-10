package sk.hrstka.controllers.auth.impl

import org.mockito.Mockito._
import org.scalatest.DoNotDiscover
import play.api.Application
import play.api.test.Helpers._
import sk.hrstka.BaseStandaloneFakeApplicationSuites
import sk.hrstka.controllers.itest.BaseControllerISpec
import sk.hrstka.models.domain.User

import scala.concurrent.Future

@DoNotDiscover
class StandaloneAuthControllerImplISpec extends BaseStandaloneFakeApplicationSuites {
  override val nestedSuites = Vector(new AuthControllerImplISpec(app))
}

@DoNotDiscover
class AuthControllerImplISpec(application: Application) extends BaseControllerISpec {
  behavior of "login"

  it should "gets HTML view with the login form" in new TestScope {
    // Prepare
    prepareMainModel()

    // Execute
    assertView(authController.login()) { content =>
      assert(content.contains("<form class=\"form-signin\" action=\"/authenticate\" method=\"post\">"))
    }

    // Verify
    verifyMainModel()
    verifyNoMore()
  }

  behavior of "logout"

  it should "not authorize anonymous user" in new TestScope {
    assertAnonymousUser(authController.logout())
  }

  it should "log out an eminent user and redirect somewhere" in new LogoutTestScope {
    logout(eminentUser)
  }

  it should "log out an admin user and redirect somewhere" in new LogoutTestScope {
    logout(adminUser)
  }

  behavior of "authenticate"

  it should "handle correct login form" in new TestScope {
    // Prepare
    when(authService.authenticate(adminUser.email, "123"))
      .thenReturn(Future.successful(Some(adminUser)))

    // Execute
    val form: Map[String, String] = Map(
      "email" -> adminUser.email.value,
      "password" -> "123"
    )
    assertResult(authController.authenticate(), form) { result =>
      assert(status(result) == SEE_OTHER)
      assert(redirectLocation(result).contains("/"))
    }

    // Verify
    verify(authService).authenticate(adminUser.email, "123")
    verifyNoMore()
  }

  it should "return bad request if login form is invalid" in new TestScope {
    // Execute
    val form: Map[String, String] = Map(
      "email" -> adminUser.email.value
    )
    assertResult(authController.authenticate(), form) { result =>
      assert(status(result) == BAD_REQUEST)
      assert(contentAsString(result) == "{\"password\":[\"This field is required\"]}")
    }

    // Verify
    verifyNoMore()
  }

  it should "return unauthorized if login data are not correct" in new TestScope {
    // Prepare
    when(authService.authenticate(adminUser.email, "123"))
      .thenReturn(Future.successful(None))

    // Execute
    val form: Map[String, String] = Map(
      "email" -> adminUser.email.value,
      "password" -> "123"
    )
    assertResult(authController.authenticate(), form) { result =>
      assert(status(result) == UNAUTHORIZED)
    }

    // Verify
    verify(authService).authenticate(adminUser.email, "123")
    verifyNoMore()
  }

  behavior of "registerView"

  it should "not authorize anonymous user" in new TestScope {
    assertAnonymousUser(authController.registerView())
  }

  it should "not authorize eminent" in new TestScope {
    assertEminentUser(authController, authController.registerView())
  }

  it should "get HTML view with a form to register a new user" in new TestScope {
    withAdminUser() {
      assertAuthView(adminUser, authController, authController.registerView()) { content =>
        assert(content.contains("<form action=\"/register\" method=\"post\">"))
      }
    }
  }

  behavior of "register"

  it should "not authorize anonymous user" in new TestScope {
    assertAnonymousUser(authController.register())
  }

  it should "not authorize eminent" in new TestScope {
    assertEminentUser(authController, authController.register())
  }

  it should "return bad request if passwords do not match" in new TestScope {
    withAdminUser(mainModel = false) {
      // Execute
      val form: Map[String, String] = Map(
        "email" -> adminUser.email.value,
        "password" -> "123",
        "passwordAgain" -> "12"
      )
      assertAuthResult(adminUser, authController, authController.register(), form) { result =>
        assert(status(result) == BAD_REQUEST)
      }
    }
  }

  it should "redirect to index if registration was successfull" in new TestScope {
    withAdminUser(mainModel = false) {
      // Prepare
      when(authService.createUser(adminUser.email, "123"))
        .thenReturn(Future.successful(()))

      // Execute
      val form: Map[String, String] = Map(
        "email" -> adminUser.email.value,
        "password" -> "123",
        "passwordAgain" -> "123"
      )
      assertAuthResult(adminUser, authController, authController.register(), form) { result =>
        assert(status(result) == SEE_OTHER)
        assert(redirectLocation(result).contains("/"))
      }

      // Verify
      verify(authService).createUser(adminUser.email, "123")
    }
  }

  private class LogoutTestScope extends TestScope {
    def logout(user: User): Unit = {
      withUser(user, mainModel = false) {
        assertAuthResult(user, authController, authController.logout()) { result =>
          assert(status(result) == SEE_OTHER)
          assert(redirectLocation(result).contains("/"))
        }
      }
      verifyNoMore()
    }
  }

  private class TestScope extends BaseAuthTestScope(application) {
    val authController = new AuthControllerImpl(
      authService,
      locationService,
      techService,
      application,
      messagesApi
    )
  }
}
