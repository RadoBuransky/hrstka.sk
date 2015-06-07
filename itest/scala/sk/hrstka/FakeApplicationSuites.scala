package sk.hrstka

import org.scalatest.Suites
import org.scalatestplus.play.OneAppPerSuite
import play.api.test.FakeApplication
import sk.hrstka.conf.RoutesISpec
import sk.hrstka.controllers.auth.impl.{AuthCompControllerImplISpec, AuthControllerImplISpec, AuthTechControllerImplISpec}

class FakeApplicationSuites extends BaseStandaloneFakeApplicationSuites {
  override val nestedSuites = Vector(
    new RoutesISpec(app),
    new AuthCompControllerImplISpec(app),
    new AuthControllerImplISpec(app),
    new AuthTechControllerImplISpec(app)
  )
}

abstract class BaseStandaloneFakeApplicationSuites extends Suites with OneAppPerSuite {
  private val application: FakeApplication = new FakeApplication(
    additionalConfiguration = Map(
      "mongodb.db" -> "hrstka-itest"
    )
  )
}