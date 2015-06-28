package sk.hrstka.controllers.impl

import play.api.mvc.Results
import play.api.test.FakeRequest
import play.api.test.Helpers._
import sk.hrstka.controllers.test.BaseControllerSpec
import sk.hrstka.test.BaseSpec

class AppControllerImplSpec extends BaseControllerSpec with Results {
  behavior of "untrail"

  it should "remove ending / and redirect" in new TestScope {
    val result = appController.untrail("abc").apply(FakeRequest())
    assert(status(result) == MOVED_PERMANENTLY)
    assert(redirectLocation(result).contains("/abc"))
  }

  behavior of "api"

  it should "get HTML view with information about REST API" in new TestScope {
    // Prepare
    prepareMainModel()

    // Execute
    assertView(appController.api()) { content =>
      assert(content.contains("<h2>API</h2>"))
    }

    // Verify
    verifyMainModel()
    verifyNoMore()
  }

  behavior of "about"

  it should "get HTML view with information about the website" in new TestScope {
    // Prepare
    prepareMainModel()

    // Execute
    assertView(appController.about()) { content =>
      assert(content.contains("<h2>O Hŕstke</h2>"))
    }

    // Verify
    verifyMainModel()
    verifyNoMore()
  }

  behavior of "addCompInfo"

  it should "get HTML view with information about how to add a company" in new TestScope {
    // Prepare
    prepareMainModel()

    // Execute
    assertView(appController.addCompInfo()) { content =>
      assert(content.contains("<h2>Ako pridať firmu</h2>"))
    }

    // Verify
    verifyMainModel()
    verifyNoMore()
  }

  private class TestScope extends BaseTestScope {
    val appController = new AppControllerImpl(
      locationService,
      techService,
      application
    )
  }
}
