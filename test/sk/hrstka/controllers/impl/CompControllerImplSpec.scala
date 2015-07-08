package sk.hrstka.controllers.impl

import org.mockito.Mockito._
import play.api.i18n.MessagesApi
import play.api.mvc.Results
import sk.hrstka.controllers.test.BaseControllerSpec
import sk.hrstka.models.domain.{CompRatingSpec, CompSpec, TechRatingSpec}
import sk.hrstka.services.{AuthService, CompService, MarkdownService}

import scala.concurrent.Future

class CompControllerImplSpec extends BaseControllerSpec with Results {
  behavior of "get"

  it should "get HTML view of a company for the provided identifier" in new TestScope {
    // Prepare
    when(compService.get(CompSpec.avitech.businessNumber))
      .thenReturn(Future.successful(CompSpec.avitech))
    when(markdownService.toHtml(CompSpec.avitech.markdownNote))
      .thenReturn(CompSpec.avitech.markdownNote)
    prepareMainModel()

    // Execute
    assertView(compController.get(CompSpec.avitech.businessNumber.value)) { result =>
      assert(result.contains("http://avitech.aero/"))
    }

    // Verify
    verifyMainModel()
    verify(compService).get(CompSpec.avitech.businessNumber)
    verify(markdownService).toHtml(CompSpec.avitech.markdownNote)
    verifyNoMore()
  }

  behavior of "women"

  it should "get HTML view for companies with the most women" in new TestScope {
    // Prepare
    when(compService.topWomen())
      .thenReturn(Future.successful(Seq(CompRatingSpec.borci, CompRatingSpec.avitech)))
    prepareMainModel()

    // Execute
    assertView(compController.women()) { result =>
      assert(result.contains("<h2>Firmy kde je veľa programátoriek</h2>"))
    }

    // Verify
    verifyMainModel()
    verify(compService).topWomen()
    verify(markdownService).toHtml(CompSpec.borci.markdownNote)
    verify(markdownService).toHtml(CompSpec.avitech.markdownNote)
    verifyNoMore()
  }

  behavior of "all"

  it should "get HTML view containing all companies" in new TestScope {
    // Prepare
    when(compService.all(None, None))
      .thenReturn(Future.successful(CompRatingSpec.all))
    prepareMainModel()

    // Execute
    assertView(compController.all()) { result =>
      assert(result.contains("<h2>Všetky firmy kde sa programuje</h2>"))
    }

    // Verify
    verifyMainModel()
    verify(compService).all(None, None)
    verify(markdownService).toHtml(CompSpec.borci.markdownNote)
    verify(markdownService).toHtml(CompSpec.avitech.markdownNote)
    verifyNoMore()
  }

  behavior of "cityTech"

  it should "get HTML view containing all companies if no city or tech is provided" in new TestScope {
    // Prepare
    when(compService.all(None, None))
      .thenReturn(Future.successful(CompRatingSpec.all))
    prepareMainModel()

    // Execute
    assertView(compController.cityTech("", "")) { result =>
      assert(result.contains("<h2>Všetky firmy kde sa programuje</h2>"))
    }

    // Verify
    verifyMainModel()
    verify(compService).all(None, None)
    verify(markdownService).toHtml(CompSpec.borci.markdownNote)
    verify(markdownService).toHtml(CompSpec.avitech.markdownNote)
    verifyNoMore()
  }

  it should "get HTML view containing companies in Bratislava" in new TestScope {
    // Prepare
    when(compService.all(city = Some(CompSpec.avitech.city.handle), None))
      .thenReturn(Future.successful(Seq(CompRatingSpec.avitech)))
    when(locationService.get(CompSpec.avitech.city.handle))
      .thenReturn(Future.successful(CompSpec.avitech.city))
    prepareMainModel(Some(CompSpec.avitech.city.handle))

    // Execute
    assertView(compController.cityTech(CompSpec.avitech.city.handle.value, "")) { result =>
      assert(result.contains("<h2>Firmy v meste Bratislava</h2>"))
    }

    // Verify
    verifyMainModel(Some(CompSpec.avitech.city.handle))
    verify(locationService).get(CompSpec.avitech.city.handle)
    verify(compService).all(Some(CompSpec.avitech.city.handle), None)
    verify(markdownService).toHtml(CompSpec.avitech.markdownNote)
    verifyNoMore()
  }

  it should "get HTML view containing companies in Bratislava that use Scala" in new TestScope {
    // Prepare
    when(compService.all(city = Some(CompSpec.avitech.city.handle), tech = Some(TechRatingSpec.scalaRating.tech.handle)))
      .thenReturn(Future.successful(Seq(CompRatingSpec.avitech)))
    when(locationService.get(CompSpec.avitech.city.handle))
      .thenReturn(Future.successful(CompSpec.avitech.city))
    when(techService.getByHandle(TechRatingSpec.scalaRating.tech.handle))
      .thenReturn(Future.successful(TechRatingSpec.scalaRating.tech))
    prepareMainModel(Some(CompSpec.avitech.city.handle))

    // Execute
    assertView(compController.cityTech(CompSpec.avitech.city.handle.value, TechRatingSpec.scalaRating.tech.handle.value)) { result =>
      assert(result.contains("<h2>Scala v meste Bratislava</h2>"))
    }

    // Verify
    verifyMainModel(Some(CompSpec.avitech.city.handle))
    verify(techService).getByHandle(TechRatingSpec.scalaRating.tech.handle)
    verify(locationService).get(CompSpec.avitech.city.handle)
    verify(compService).all(Some(CompSpec.avitech.city.handle), Some(TechRatingSpec.scalaRating.tech.handle))
    verify(markdownService).toHtml(CompSpec.avitech.markdownNote)
    verifyNoMore()
  }

  private class TestScope extends BaseTestScope {
    val compService = mock[CompService]
    val markdownService = mock[MarkdownService]
    val authService = mock[AuthService]
    val messagesApi = mock[MessagesApi]
    val compController = new CompControllerImpl(
      compService,
      markdownService,
      authService,
      techService,
      locationService,
      application,
      messagesApi
    )

    override def verifyNoMore(): Unit = {
      verifyNoMoreInteractions(compService)
      verifyNoMoreInteractions(markdownService)
      verifyNoMoreInteractions(authService)
      verifyNoMoreInteractions(messagesApi)
      super.verifyNoMore()
    }
  }
}