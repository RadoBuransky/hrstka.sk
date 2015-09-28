package sk.hrstka.services.impl

import sk.hrstka.services.{LocationService, TechService}
import sk.hrstka.test.BaseSpec

class SearchTermServiceImplSpec extends BaseSpec {
  behavior of "tokens"

  it should "split raw query using regular expression" in new TestScope {
    assert(service.tokens(" Scala  Bratislava,wTF    c++;pl/sql") == Seq("scala", "bratislava", "wtf", "c++", "pl/sql"))
  }

  it should "work for empty query" in new TestScope {
    assert(service.tokens("") == Seq.empty)
  }

  it should "work for virtually empty query" in new TestScope {
    assert(service.tokens("  ") == Seq.empty)
  }

  class TestScope {
    val techService = mock[TechService]
    val locationService = mock[LocationService]
    def service = new SearchTermServiceImpl(techService, locationService)
  }
}
