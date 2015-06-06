package sk.hrstka.controllers.impl

import play.api.mvc.Results
import play.api.test.FakeRequest
import sk.hrstka.test.BaseSpec

class AppControllerImplSpec extends BaseSpec with Results {
  behavior of "untrail"

  it should "remove ending / and redirect" in new TestScope {
    assert(appController.untrail("abc").apply(FakeRequest()).futureValue == MovedPermanently("/abc"))
  }

  private class TestScope {
    val appController = new AppControllerImpl()
  }
}
