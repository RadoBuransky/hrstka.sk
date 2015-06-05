package sk.hrstka.conf

import org.scalatest.FlatSpec
import org.scalatestplus.play.OneAppPerSuite
import play.api.test.Helpers._
import play.api.test.{FakeApplication, FakeRequest}
import sk.hrstka.common.Logging

class RoutesISpec extends FlatSpec with OneAppPerSuite with Logging {
  override implicit lazy val app: FakeApplication = new FakeApplication(
    additionalConfiguration = Map(
      "mongodb.db" -> "hrstka-itest"
    )
  )

  behavior of "GET /"

  it should "return HTML of all companies" in {
    val Some(result) = route(app, FakeRequest(GET, "/"))
    assert(status(result) == OK)
    assert(contentType(result).contains("text/html"))
    assert(contentAsString(result).contains("<h2>Firmy na Slovensku kde sa programuje</h2>"))
  }
}
