package sk.hrstka.controllers.impl

import org.mockito.Mockito._
import play.api.libs.json.{JsArray, JsValue, Json}
import play.api.mvc.Results
import play.api.test.FakeRequest
import play.api.test.Helpers._
import sk.hrstka.models.api.JsonFormats._
import sk.hrstka.models.api.{CompFactory, TechFactory}
import sk.hrstka.models.domain._
import sk.hrstka.services.{CompService, LocationService, TechService}
import sk.hrstka.test.BaseSpec

import scala.concurrent.Future

class ApiControllerImplSpec extends BaseSpec with Results {
  behavior of "comps"

  it should "return JSON of all companies" in new TestScope {
    // Prepare
    when(compService.all(None, None))
      .thenReturn(Future.successful(CompRatingSpec.all))

    // Execute
    assert(contentAsJson(apiController.comps().apply(FakeRequest())) == Json.toJson(CompRatingSpec.all.map { compRating =>
      CompFactory.fromDomain(compRating, sk.hrstka.controllers.routes.CompController.get(compRating.comp.id.value).absoluteURL()(FakeRequest()))
    }))

    // Verify
    verify(compService).all(None, None)
    verifyNoMore()
  }

  behavior of "techs"

  it should "return JSON of all technologies" in new TestScope {
    // Prepare
    when(techService.allRatings())
      .thenReturn(Future.successful(TechRatingSpec.allRatings))

    // Execute
    assert(contentAsJson(apiController.techs().apply(FakeRequest())) == Json.toJson(TechRatingSpec.allRatings.map(TechFactory.fromDomain)))

    // Verify
    verify(techService).allRatings()
    verifyNoMore()
  }

  behavior of "cities"

  it should "return JSON of all cities" in new TestScope {
    def cityToJson(city: City): JsValue = {
      Json.obj(
        "handle" -> city.handle.value,
        "sk" -> city.sk
      )
    }

    // Prepare
    when(locationService.all())
      .thenReturn(Future.successful(CitySpec.all))

    // Execute
    assert(contentAsJson(apiController.cities().apply(FakeRequest())) == JsArray(CitySpec.all.map(cityToJson)))

    // Verify
    verify(locationService).all()
    verifyNoMore()
  }

  private class TestScope {
    val compService = mock[CompService]
    val techService = mock[TechService]
    val locationService = mock[LocationService]
    val apiController = new ApiControllerImpl(compService, techService, locationService)

    def verifyNoMore(): Unit = {
      verifyNoMoreInteractions(compService)
      verifyNoMoreInteractions(techService)
      verifyNoMoreInteractions(locationService)
    }
  }
}
