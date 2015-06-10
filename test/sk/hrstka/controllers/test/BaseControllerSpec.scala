package sk.hrstka.controllers.test

import org.mockito.Mockito._
import org.mockito.internal.util.MockUtil
import play.api.mvc.{Action, AnyContent, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Application, Mode}
import sk.hrstka.models.domain.{Handle, CitySpec, TechRatingSpec}
import sk.hrstka.services.{LocationService, TechService}
import sk.hrstka.test.BaseSpec

import scala.concurrent.Future

abstract class BaseControllerSpec extends BaseSpec {
  protected def assertView(action: Action[AnyContent])(f: (String) => Unit): Unit = {
    val result = action(FakeRequest())
    assert(status(result) == OK)
    assert(contentType(result).contains("text/html"))
    f(contentAsString(result))
  }

  protected abstract class BaseTestScope(val application: Application) {
    def this() = this(mock[Application])
    val techService = mock[TechService]
    val locationService = mock[LocationService]
    private lazy val applicationIsAMock = new MockUtil().isMock(application)

    def prepareMainModel(cityHandle: Option[Handle] = None): Unit = {
      when(locationService.all())
        .thenReturn(Future.successful(CitySpec.all))
      when(techService.allUsed(cityHandle))
        .thenReturn(Future.successful(TechRatingSpec.allTechs))
      if (applicationIsAMock) {
        when(application.mode)
          .thenReturn(Mode.Test)
      }
    }

    protected def assertResult(action: Action[AnyContent],
                               form: Map[String, String] = Map.empty)(f: (Future[Result]) => Unit): Unit = {
      val requestWithForm = if (form.isEmpty)
        FakeRequest()
      else
        FakeRequest().withFormUrlEncodedBody(form.toSeq:_*)

      val result = action(requestWithForm)
      f(result)
    }

    def verifyMainModel(cityHandle: Option[Handle] = None): Unit = {
      verify(locationService, atLeastOnce()).all()
      verify(techService, atLeastOnce()).allUsed(cityHandle)
      if (applicationIsAMock) {
        verify(application, atLeastOnce).mode
      }
    }

    def verifyNoMore(): Unit = {
      verifyNoMoreInteractions(techService)
      verifyNoMoreInteractions(locationService)
      verifyNoMoreInteractions(application)}
  }
}
