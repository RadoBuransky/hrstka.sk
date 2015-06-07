package sk.hrstka.controllers.test

import org.mockito.Mockito._
import org.mockito.internal.util.MockUtil
import play.api.mvc.Result
import play.api.test.Helpers._
import play.api.{Application, Mode}
import sk.hrstka.models.domain.{CitySpec, TechRatingSpec}
import sk.hrstka.services.{LocationService, TechService}
import sk.hrstka.test.BaseSpec

import scala.concurrent.Future

abstract class BaseControllerSpec extends BaseSpec {
  protected def assertView(result: Future[Result])(f: (String) => Unit): Unit = {
    assert(status(result) == OK)
    assert(contentType(result).contains("text/html"))
    f(contentAsString(result))
  }

  protected abstract class BaseTestScope(val application: Application) {
    def this() = this(mock[Application])
    val techService = mock[TechService]
    val locationService = mock[LocationService]
    private lazy val applicationIsAMock = new MockUtil().isMock(application)

    def prepareMainModel(): Unit = {
      when(locationService.all())
        .thenReturn(Future.successful(CitySpec.all))
      when(techService.allRatings())
        .thenReturn(Future.successful(TechRatingSpec.allRatings))
      if (applicationIsAMock) {
        when(application.mode)
          .thenReturn(Mode.Test)
      }
    }
    def verifyMainModel(): Unit = {
      verify(locationService, atLeastOnce()).all()
      verify(techService, atLeastOnce()).allRatings()
      if (applicationIsAMock) {
        verify(application, times(2)).mode
      }
    }

    def verifyNoMore(): Unit = {
      verifyNoMoreInteractions(techService)
      verifyNoMoreInteractions(locationService)
      verifyNoMoreInteractions(application)}
  }
}
