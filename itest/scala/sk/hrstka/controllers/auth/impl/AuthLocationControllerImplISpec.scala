package sk.hrstka.controllers.auth.impl

import org.scalatest.DoNotDiscover
import play.api.Application
import play.api.test.Helpers._
import sk.hrstka.BaseStandaloneFakeApplicationSuites
import sk.hrstka.controllers.itest.BaseControllerISpec
import org.mockito.Mockito._
import sk.hrstka.models.domain._

import scala.concurrent.Future

@DoNotDiscover
class StandaloneAuthLocationControllerImplISpec extends BaseStandaloneFakeApplicationSuites {
  override val nestedSuites = Vector(new AuthLocationControllerImplISpec(app))
}

@DoNotDiscover
class AuthLocationControllerImplISpec(application: Application) extends BaseControllerISpec {
  behavior of "all"

  it should "not authorize anonymous user" in new TestScope {
    assertAnonymousUser(authLocationController.all())
  }

  it should "get HTML view with all locations" in new TestScope {
    withEminentUser() {
      // Prepare
      when(locationService.countries())
        .thenReturn(Future.successful(CountrySpec.all))
      when(locationService.cities())
        .thenReturn(Future.successful(CitySpec.all))

      // Execute
      assertAuthView(eminentUser, authLocationController, authLocationController.all) { content =>
        assert(content.contains("<h2>Cities</h2>"))
      }

      // Verify
      verify(locationService, times(2)).cities()
      verify(locationService).countries()
    }
  }

  behavior of "add"

  it should "not authorize anonymous user" in new TestScope {
    assertAnonymousUser(authLocationController.add())
  }

  it should "handle submitted form" in new TestScope {
    withEminentUser(mainModel = false) {
      // Prepare
      val city = City(
        handle = HandleFactory.fromHumanName("Bratislava"),
        country = Slovakia,
        en = "Bratislava"
      )
      when(locationService.getCountryByCode(Slovakia.code))
        .thenReturn(Future.successful(Slovakia))
      when(locationService.upsert(city))
        .thenReturn(Future.successful(city.handle))

      // Execute
      val form: Map[String, String] = Map(
        "countryCode" -> city.country.code.value,
        "city" -> city.en
      )
      assertAuthResult(eminentUser, authLocationController, authLocationController.add(), form) { result =>
        assert(status(result) == SEE_OTHER)
        assert(redirectLocation(result).contains("/cities"))
      }

      // Verify
      verify(locationService).upsert(city)
      verify(locationService).getCountryByCode(Slovakia.code)
    }
  }

  behavior of "remove"

  it should "not authorize anonymous user" in new TestScope {
    assertAnonymousUser(authLocationController.remove("SK"))
  }

  it should "remove a technology and redirect" in new TestScope {
    withEminentUser(mainModel = false) {
      // Prepare
      when(locationService.remove(CitySpec.bratislava.handle))
        .thenReturn(Future.successful(CitySpec.bratislava.handle))

      // Execute
      assertAuthResult(eminentUser, authLocationController, authLocationController.remove(CitySpec.bratislava.handle.value)) { result =>
        assert(status(result) == SEE_OTHER)
        assert(redirectLocation(result).contains("/cities"))
      }

      // Verify
      verify(locationService).remove(CitySpec.bratislava.handle)
    }
  }

  private class TestScope extends BaseAuthTestScope(application) {
    val authLocationController = new AuthLocationControllerImpl(
      authService,
      locationService,
      techService,
      application,
      messagesApi
    )
  }
}
