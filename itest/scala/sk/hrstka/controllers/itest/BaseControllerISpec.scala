package sk.hrstka.controllers.itest

import jp.t2v.lab.play2.auth.AuthConfig
import org.mockito.Mockito._
import play.api.Application
import play.api.http.HeaderNames
import play.api.i18n.MessagesApi
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import sk.hrstka.controllers.auth.impl.HrstkaAuthConfig
import sk.hrstka.controllers.test.BaseControllerSpec
import sk.hrstka.models.domain.UserSpec
import sk.hrstka.services.AuthService

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

abstract class BaseControllerISpec extends BaseControllerSpec with Results {
  implicit class AuthFakeRequest[A](fakeRequest: FakeRequest[A]) {
    def withLoggedIn(implicit config: AuthConfig): config.Id => FakeRequest[A] = { id =>
      val token = Await.result(config.idContainer.startNewSession(id, config.sessionTimeoutInSeconds)(fakeRequest, global), 10.seconds)
      // This is just a fucking hack!

      // Create auth cookie and add it to the dummy result
      val dummyResult = config.tokenAccessor.put(token)(Ok)(fakeRequest)

      // Decode cookies from the dummy result headers
      val cookies = Cookies.decodeCookieHeader(dummyResult.header.headers(HeaderNames.SET_COOKIE))

      // Copy cookies from the dummy result to the request
      fakeRequest.withCookies(cookies:_*)
    }
  }

  protected class BaseAuthTestScope(application: Application) extends BaseTestScope(application) {
    val authService = mock[AuthService]
    lazy val messagesApi = application.injector.instanceOf[MessagesApi]
    lazy val authUser = UserSpec.rado

    def withAuthorisedUser(testBody: => Unit): Unit = {
      // Prepare
      prepareAuth()
      prepareMainModel()

      testBody

      // Verify
      verifyMainModel()
      verifyAuth()
      verifyNoMore()
    }

    def postWithAuthorisedUser(testBody: => Unit): Unit = {
      // Prepare
      prepareAuth()

      testBody

      // Verify
      verifyAuth()
      verifyNoMore()
    }

    def prepareAuth(): Unit = {
      when(authService.findByEmail(authUser.email))
        .thenReturn(Future.successful(Some(authUser)))
    }

    protected def assertAuthResult(hrstkaAuthConfig: HrstkaAuthConfig,
                                   action: Action[AnyContent],
                                   form: Map[String, String] = Map.empty)(f: (Future[Result]) => Unit): Unit = {
      val reuestWithUser = FakeRequest().withLoggedIn(hrstkaAuthConfig)(authUser.email)
      val requestWithForm = if (form.isEmpty)
        reuestWithUser
      else
        reuestWithUser.withFormUrlEncodedBody(form.toSeq:_*)

      val result = action(requestWithForm)
      f(result)
    }

    protected def assertAuthView(hrstkaAuthConfig: HrstkaAuthConfig, action: Action[AnyContent])(f: (String) => Unit): Unit = {
      assertAuthResult(hrstkaAuthConfig, action) { result =>
        assert(status(result) == OK)
        assert(contentType(result).contains("text/html"))
        f(contentAsString(result))
      }
    }

    protected def assertAnonymousUser(action: Action[AnyContent]): Unit = {
      assert(status(action.apply(FakeRequest())) == UNAUTHORIZED)
    }


    def verifyAuth(): Unit = {
      verify(authService).findByEmail(authUser.email)
    }

    override def verifyNoMore(): Unit = {
      verifyNoMoreInteractions(authService)
    }
  }
}
