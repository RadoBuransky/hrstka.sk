package sk.hrstka.controllers.auth.impl

import org.mockito.Mockito._
import org.scalatest.DoNotDiscover
import play.api.Application
import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import sk.hrstka.BaseStandaloneFakeApplicationSuites
import sk.hrstka.controllers.itest.BaseControllerISpec
import sk.hrstka.models.domain.{TechRatingSpec, UserSpec}
import sk.hrstka.services.{AuthService, CompService}

import scala.concurrent.Future

class StandaloneAuthCompControllerImplISpec extends BaseStandaloneFakeApplicationSuites {
  override val nestedSuites = Vector(new AuthCompControllerImplISpec(app))
}

@DoNotDiscover
class AuthCompControllerImplISpec(application: Application) extends BaseControllerISpec {
  behavior of "addForm"

  it should "not authorize visitors" in new TestScope {
    assertVisitorUnauthorised(authCompController.addForm())
  }

  it should "gets HTML view with a form to add a company" in new TestScope {
    // Prepare
    when(authService.findByEmail(UserSpec.rado.email))
      .thenReturn(Future.successful(Some(UserSpec.rado)))
    when(techService.allRatings())
      .thenReturn(Future.successful(TechRatingSpec.allRatings))
    prepareMainModel()

    // Execute
    val fakeRequest = FakeRequest().withLoggedIn(authCompController)(UserSpec.rado.email)
    assertView(authCompController.addForm()(fakeRequest)) { content =>
      assert(content.contains("<form action=\"/programovanie/firma\" method=\"post\">"))
    }

    // Verify
    verify(locationService).all()
    verify(techService, times(2)).allRatings()
    verify(authService).findByEmail(UserSpec.rado.email)
    verifyNoMore()
  }

  private class TestScope extends BaseTestScope(application) {
    val compService = mock[CompService]
    val authService = mock[AuthService]
    val messagesApi = mock[MessagesApi]
    val authCompController = new AuthCompControllerImpl(
      compService,
      authService,
      techService,
      locationService,
      application,
      messagesApi
    )

    override def verifyNoMore(): Unit = {
      verifyNoMoreInteractions(compService)
      verifyNoMoreInteractions(authService)
      verifyNoMoreInteractions(messagesApi)
    }
  }
}
