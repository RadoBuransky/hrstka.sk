package sk.hrstka.services.impl

import sk.hrstka.models.domain._
import sk.hrstka.services.{LocationService, TechService}
import sk.hrstka.test.BaseSpec
import org.mockito.Mockito._

import scala.concurrent.Future

class SearchTermServiceImplSpec extends BaseSpec {
  behavior of "compSearch"

  it should "find location term" in new TestScope {
    val result = futureValue(service.compSearchQuery("bratislava"))
    assert(result == CompSearchQuery(Set(CitySearchTerm(CitySpec.bratislava.handle))))
  }

  it should "find tech term" in new TestScope {
    val result = futureValue(service.compSearchQuery("scala"))
    assert(result == CompSearchQuery(Set(TechSearchTerm(TechRatingSpec.scalaRating.tech.handle))))
  }

  it should "find multiple location and tech terms" in new TestScope {
    val result = futureValue(service.compSearchQuery(" Scala bratislava Košice akka"))
    assert(result ==
      CompSearchQuery(
        Set(
          CitySearchTerm(CitySpec.bratislava.handle),
          CitySearchTerm(CitySpec.kosice.handle),
          TechSearchTerm(TechRatingSpec.scalaRating.tech.handle),
          TechSearchTerm(TechRatingSpec.akkaRating.tech.handle)
        )))
  }

  behavior of "queryToTokens"

  it should "split raw query using regular expression" in new TestScope {
    assert(service.queryToTokens(" Scala  Bratislava,wTF    c++;pl/sql") == Set("scala", "bratislava", "wtf", "c++", "pl/sql"))
  }

  it should "work for empty query" in new TestScope {
    assert(service.queryToTokens("") == Set.empty)
  }

  it should "work for virtually empty query" in new TestScope {
    assert(service.queryToTokens("  ") == Set.empty)
  }

  it should "remove duplicates" in new TestScope {
    assert(service.queryToTokens(" scALA   Scala scala") == Set("scala"))
  }

  class TestScope {
    val techService = mock[TechService]
    val locationService = mock[LocationService]

    when(techService.allRatings()).thenReturn(Future.successful(TechRatingSpec.allRatings))
    when(locationService.allCities()).thenReturn(Future.successful(CitySpec.all))

    def service = new CompSearchServiceImpl(techService, locationService)
  }
}
