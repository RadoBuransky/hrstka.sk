package sk.hrstka.controllers.impl

import org.mockito.Mockito._
import play.api.i18n.MessagesApi
import play.api.mvc.Results
import play.api.test.FakeRequest
import sk.hrstka.controllers.test.BaseControllerSpec
import sk.hrstka.models.domain.{CompSpec, TechRatingSpec}
import sk.hrstka.services.{AuthService, CompService}

import scala.concurrent.Future

class CompControllerImplSpec extends BaseControllerSpec with Results {
  behavior of "get"

  it should "get HTML view of a company for the provided identifier" in new TestScope {
    // Prepare
    when(compService.get(CompSpec.avitech.id))
      .thenReturn(Future.successful(CompSpec.avitech))
    prepareMainModel()

    // Execute
    assertView(compController.get(CompSpec.avitech.id.value).apply(FakeRequest())) { result =>
      assert(result.contains("<h2><a href=\"http://avitech.aero/\" target=\"_blank\">Avitech</a></h2>"))
    }

    // Verify
    verifyMainModel()
    verify(compService).get(CompSpec.avitech.id)
    verifyNoMore()
  }

  behavior of "women"

  it should "get HTML view for companies with the most women" in new TestScope {
    // Prepare
    when(compService.topWomen())
      .thenReturn(Future.successful(Seq(CompSpec.borci, CompSpec.avitech)))
    prepareMainModel()

    // Execute
    assertView(compController.women().apply(FakeRequest())) { result =>
      assert(result.contains("<h2>Firmy kde je veľa programátoriek</h2>"))
      assert(result.contains(">Borci</a></h3>"))
      assert(result.contains(">Avitech</a></h3>"))
    }

    // Verify
    verifyMainModel()
    verify(compService).topWomen()
    verifyNoMore()
  }

  behavior of "all"

  it should "get HTML view containing all companies" in new TestScope {
    // Prepare
    when(compService.all(None, None))
      .thenReturn(Future.successful(CompSpec.all))
    prepareMainModel()

    // Execute
    assertView(compController.all().apply(FakeRequest())) { result =>
      assert(result.contains("<h2>Firmy na Slovensku kde sa programuje</h2>"))
      assert(result.contains(">Borci</a></h3>"))
      assert(result.contains(">Avitech</a></h3>"))
    }

    // Verify
    verifyMainModel()
    verify(compService).all(None, None)
    verifyNoMore()
  }

  behavior of "cityTech"

  it should "get HTML view containing all companies if no city or tech is provided" in new TestScope {
    // Prepare
    when(compService.all(None, None))
      .thenReturn(Future.successful(CompSpec.all))
    prepareMainModel()

    // Execute
    assertView(compController.cityTech("", "").apply(FakeRequest())) { result =>
      assert(result.contains("<h2>Firmy na Slovensku kde sa programuje</h2>"))
      assert(result.contains(">Borci</a></h3>"))
      assert(result.contains(">Avitech</a></h3>"))
    }

    // Verify
    verifyMainModel()
    verify(compService).all(None, None)
    verifyNoMore()
  }

  it should "get HTML view containing companies in Bratislava" in new TestScope {
    // Prepare
    when(compService.all(city = Some(CompSpec.avitech.city.handle), None))
      .thenReturn(Future.successful(Seq(CompSpec.avitech)))
    when(locationService.get(CompSpec.avitech.city.handle))
      .thenReturn(Future.successful(CompSpec.avitech.city))
    prepareMainModel(Some(CompSpec.avitech.city.handle))

    // Execute
    assertView(compController.cityTech(CompSpec.avitech.city.handle.value, "").apply(FakeRequest())) { result =>
      assert(result.contains("<h2>Firmy v meste Bratislava</h2>"))
      assert(result.contains(">Avitech</a></h3>"))
    }

    // Verify
    verifyMainModel(Some(CompSpec.avitech.city.handle))
    verify(locationService).get(CompSpec.avitech.city.handle)
    verify(compService).all(Some(CompSpec.avitech.city.handle), None)
    verifyNoMore()
  }

  it should "get HTML view containing companies in Bratislava that use Scala" in new TestScope {
    // Prepare
    when(compService.all(city = Some(CompSpec.avitech.city.handle), tech = Some(TechRatingSpec.scalaRating.tech.handle)))
      .thenReturn(Future.successful(Seq(CompSpec.avitech)))
    when(locationService.get(CompSpec.avitech.city.handle))
      .thenReturn(Future.successful(CompSpec.avitech.city))
    when(techService.getByHandle(TechRatingSpec.scalaRating.tech.handle))
      .thenReturn(Future.successful(TechRatingSpec.scalaRating.tech))
    prepareMainModel(Some(CompSpec.avitech.city.handle))

    // Execute
    assertView(compController.cityTech(CompSpec.avitech.city.handle.value, TechRatingSpec.scalaRating.tech.handle.value).apply(FakeRequest())) { result =>
      assert(result.contains("<h2>Firmy v meste Bratislava používajúce Scala</h2>"))
      assert(result.contains(">Avitech</a></h3>"))
    }

    // Verify
    verifyMainModel(Some(CompSpec.avitech.city.handle))
    verify(techService).getByHandle(TechRatingSpec.scalaRating.tech.handle)
    verify(locationService).get(CompSpec.avitech.city.handle)
    verify(compService).all(Some(CompSpec.avitech.city.handle), Some(TechRatingSpec.scalaRating.tech.handle))
    verifyNoMore()
  }

  private class TestScope extends BaseTestScope {
    val compService = mock[CompService]
    val authService = mock[AuthService]
    val messagesApi = mock[MessagesApi]
    val compController = new CompControllerImpl(
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
      super.verifyNoMore()
    }
  }
}