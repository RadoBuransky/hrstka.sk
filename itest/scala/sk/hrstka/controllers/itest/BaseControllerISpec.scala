package sk.hrstka.controllers.itest

import jp.t2v.lab.play2.auth.AuthConfig
import play.api.http.HeaderNames
import play.api.mvc.{Cookies, Results}
import play.api.test.FakeRequest
import sk.hrstka.controllers.test.BaseControllerSpec

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.duration._

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
}
