package sk.hrstka.controllers.impl.test

import play.api.{Mode, Application}
import play.api.mvc.Result
import play.api.test.Helpers._
import sk.hrstka.models.domain.{TechRatingSpec, CitySpec}
import sk.hrstka.services.{LocationService, TechService}
import sk.hrstka.test.BaseSpec
import org.mockito.Mockito._

import scala.concurrent.Future

abstract class BaseControllerSpec extends BaseSpec {
  protected def assertView(result: Future[Result])(f: (String) => Unit): Unit = {
    assert(status(result) == OK)
    assert(contentType(result).contains("text/html"))
    f(contentAsString(result))
  }

  protected abstract class BaseTestScope {
    val techService = mock[TechService]
    val locationService = mock[LocationService]
    val application = mock[Application]

    def prepareMainModel(): Unit = {
      when(locationService.all())
        .thenReturn(Future.successful(CitySpec.all))
      when(techService.allRatings())
        .thenReturn(Future.successful(TechRatingSpec.allRatings))
      when(application.mode)
        .thenReturn(Mode.Test)
    }

    def verifyMainModel(): Unit = {
      verify(locationService).all()
      verify(techService).allRatings()
      verify(application, times(2)).mode
    }

    def verifyNoMore(): Unit = {
      verifyNoMoreInteractions(techService)
      verifyNoMoreInteractions(locationService)
      verifyNoMoreInteractions(application)}
  }
}
