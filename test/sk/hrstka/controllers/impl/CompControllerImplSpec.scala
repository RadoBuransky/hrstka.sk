package sk.hrstka.controllers.impl

import org.mockito.Mockito._
import play.api.i18n.MessagesApi
import play.api.mvc.Results
import sk.hrstka.controllers.test.BaseControllerSpec
import sk.hrstka.models.domain._
import sk.hrstka.services.{AuthService, CompSearchService, CompService, MarkdownService}

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
      assert(result.contains("<h2>Companies with many women programmers</h2>"))
    }

    // Verify
    verifyMainModel()
    verify(compService).topWomen()
    verify(markdownService).toHtml(CompSpec.borci.markdownNote)
    verify(markdownService).toHtml(CompSpec.avitech.markdownNote)
    verifyNoMore()
  }

  behavior of "search"

  it should "get HTML view containing all companies" in new TestScope {
    // Prepare
    val compSearchQuery = CompSearchQuery(Set.empty)
    when(compSearchService.compSearchQuery(""))
      .thenReturn(Future.successful(compSearchQuery))
    when(compService.search(compSearchQuery))
      .thenReturn(Future.successful(CompRatingSpec.all))
    prepareMainModel()

    // Execute
    assertView(compController.search()) { result =>
      assert(result.contains("<h2>All tech companies</h2>"))
    }

    // Verify
    verifyMainModel()
    verify(compService).search(compSearchQuery)
    verify(compSearchService).compSearchQuery("")
    verify(markdownService).toHtml(CompSpec.borci.markdownNote)
    verify(markdownService).toHtml(CompSpec.avitech.markdownNote)
    verifyNoMore()
  }

  behavior of "cityTech"

  it should "get HTML view containing all companies if no city or tech is provided" in new TestScope {
    // Prepare
    val compSearchQuery = CompSearchQuery(Set.empty)
    when(compService.search(compSearchQuery))
      .thenReturn(Future.successful(CompRatingSpec.all))
    prepareMainModel()

    // Execute
    assertView(compController.cityTech("", "")) { result =>
      assert(result.contains("<h2>All tech companies</h2>"))
    }

    // Verify
    verifyMainModel()
    verify(compService).search(compSearchQuery)
    verify(markdownService).toHtml(CompSpec.borci.markdownNote)
    verify(markdownService).toHtml(CompSpec.avitech.markdownNote)
    verifyNoMore()
  }

  it should "get HTML view containing companies in Bratislava" in new TestScope {
    // Prepare
    val compSearchQuery = CompSearchQuery(Set(CitySearchTerm(CitySpec.bratislava.handle)))
    when(compService.search(compSearchQuery))
      .thenReturn(Future.successful(Seq(CompRatingSpec.avitech)))
    when(locationService.city(CitySpec.bratislava.handle))
      .thenReturn(Future.successful(CitySpec.bratislava))
    prepareMainModel()

    // Execute
    assertView(compController.cityTech(CitySpec.bratislava.handle.value, "")) { result =>
      assert(result.contains("<h2>Tech companies in Bratislava city</h2>"))
    }

    // Verify
    verifyMainModel()
    verify(locationService).city(CitySpec.bratislava.handle)
    verify(compService).search(compSearchQuery)
    verify(markdownService).toHtml(CompSpec.avitech.markdownNote)
    verifyNoMore()
  }

  it should "get HTML view containing companies in Bratislava that use Scala" in new TestScope {
    // Prepare
    val compSearchQuery = CompSearchQuery(Set(CitySearchTerm(CitySpec.bratislava.handle), TechSearchTerm(TechRatingSpec.scalaRating.tech.handle)))
    when(compService.search(compSearchQuery))
      .thenReturn(Future.successful(Seq(CompRatingSpec.avitech)))
    when(locationService.city(CitySpec.bratislava.handle))
      .thenReturn(Future.successful(CitySpec.bratislava))
    when(techService.getByHandle(TechRatingSpec.scalaRating.tech.handle))
      .thenReturn(Future.successful(TechRatingSpec.scalaRating.tech))
    prepareMainModel()

    // Execute
    assertView(compController.cityTech(CitySpec.bratislava.handle.value, TechRatingSpec.scalaRating.tech.handle.value)) { result =>
      assert(result.contains("<h2>Tech companies that use Scala in Bratislava city</h2>"))
    }

    // Verify
    verifyMainModel()
    verify(techService).getByHandle(TechRatingSpec.scalaRating.tech.handle)
    verify(locationService).city(CitySpec.bratislava.handle)
    verify(compService).search(compSearchQuery)
    verify(markdownService).toHtml(CompSpec.avitech.markdownNote)
    verifyNoMore()
  }

  private class TestScope extends BaseTestScope {
    val compService = mock[CompService]
    val compSearchService = mock[CompSearchService]
    val markdownService = mock[MarkdownService]
    val authService = mock[AuthService]
    val messagesApi = mock[MessagesApi]
    val compController = new CompControllerImpl(
      compService,
      compSearchService,
      markdownService,
      authService,
      techService,
      locationService,
      application,
      messagesApi
    )

    override def verifyNoMore(): Unit = {
      verifyNoMoreInteractions(compService)
      verifyNoMoreInteractions(compSearchService)
      verifyNoMoreInteractions(markdownService)
      verifyNoMoreInteractions(authService)
      verifyNoMoreInteractions(messagesApi)
      super.verifyNoMore()
    }
  }
}