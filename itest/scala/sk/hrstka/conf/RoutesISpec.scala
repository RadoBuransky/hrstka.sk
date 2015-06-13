package sk.hrstka.conf

import org.scalatest.{DoNotDiscover, FlatSpec}
import play.api.Application
import play.api.test.FakeRequest
import play.api.test.Helpers._
import sk.hrstka.common.Logging

@DoNotDiscover
class RoutesISpec(application: Application) extends FlatSpec with Logging {
  behavior of "GET /"

  it should "return HTML of all companies" in {
    val Some(result) = route(application, FakeRequest(GET, "/"))
    assert(status(result) == OK)
    assert(contentType(result).contains("text/html"))
    assert(contentAsString(result).contains("<h2>Všetky firmy kde sa programuje</h2>"))
  }
}
