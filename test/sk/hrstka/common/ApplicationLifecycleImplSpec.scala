package sk.hrstka.common

import org.mockito.Mockito._
import sk.hrstka.repositories.scripts.DbManager
import sk.hrstka.test.BaseSpec

import scala.concurrent.Future

class ApplicationLifecycleImplSpec extends BaseSpec {
  behavior of "constructor"

  it should "invoke onStart" in {
    // Prepare
    val dbManager = mock[DbManager]
    when(dbManager.applicationInit())
      .thenReturn(Future.successful(()))

    // Execute
    val applicationLifecycle = new ApplicationLifecycleImpl(dbManager)

    // Verify
    verify(dbManager).applicationInit()
    verifyNoMoreInteractions(dbManager)
  }
}
