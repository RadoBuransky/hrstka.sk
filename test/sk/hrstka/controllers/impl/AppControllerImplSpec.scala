package sk.hrstka.controllers.impl

import play.api.mvc.Results
import play.api.test.FakeRequest
import play.api.test.Helpers._
import sk.hrstka.test.BaseSpec

class AppControllerImplSpec extends BaseSpec with Results {
  behavior of "untrail"

  it should "remove ending / and redirect" in new TestScope {
    val result = appController.untrail("abc").apply(FakeRequest())
    assert(status(result) == MOVED_PERMANENTLY)
    assert(redirectLocation(result).contains("/abc"))
  }

  private class TestScope {
    val appController = new AppControllerImpl()
  }
}
