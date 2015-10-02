package sk.hrstka.services.impl

import sk.hrstka.models.domain._
import sk.hrstka.services.{LocationService, TechService}
import sk.hrstka.test.BaseSpec
import org.mockito.Mockito._

import scala.concurrent.Future

class CompSearchServiceImplSpec extends BaseSpec {
  behavior of "rank"

  it should "return match for an empty query" in new TestScope {
    assert(service.rank(CompSearchQuery(Set.empty), CompSpec.avitech) != NoMatchRank)
  }

  it should "match a tech" in new TestScope {
    assert(service.rank(CompSearchQuery(Set(TechSearchTerm(TechRatingSpec.scalaRating.tech.handle))), CompSpec.avitech) != NoMatchRank)
  }

  it should "match a city" in new TestScope {
    assert(service.rank(CompSearchQuery(Set(CitySearchTerm(CitySpec.bratislava.handle))), CompSpec.avitech) != NoMatchRank)
  }

  it should "match a tech and a city" in new TestScope {
    assert(service.rank(CompSearchQuery(
      Set(
        CitySearchTerm(CitySpec.bratislava.handle),
        TechSearchTerm(TechRatingSpec.scalaRating.tech.handle)
      )), CompSpec.avitech) != NoMatchRank)
  }

  it should "not match a tech" in new TestScope {
    assert(service.rank(CompSearchQuery(Set(TechSearchTerm(TechRatingSpec.phpRating.tech.handle))), CompSpec.avitech) == NoMatchRank)
  }

  it should "not match a city" in new TestScope {
    assert(service.rank(CompSearchQuery(Set(CitySearchTerm(CitySpec.noveZamky.handle))), CompSpec.avitech) == NoMatchRank)
  }

  it should "not match if a city matches but tech does not" in new TestScope {
    assert(service.rank(CompSearchQuery(
      Set(
        CitySearchTerm(CitySpec.bratislava.handle),
        TechSearchTerm(TechRatingSpec.phpRating.tech.handle)
      )), CompSpec.avitech) == NoMatchRank)
  }

  it should "not match if a tech matches but city does not" in new TestScope {
    assert(service.rank(CompSearchQuery(
      Set(
        CitySearchTerm(CitySpec.noveZamky.handle),
        TechSearchTerm(TechRatingSpec.scalaRating.tech.handle)
      )), CompSpec.avitech) == NoMatchRank)
  }

  it should "not match if one tech matches but other tech does not" in new TestScope {
    assert(service.rank(CompSearchQuery(
      Set(
        TechSearchTerm(TechRatingSpec.scalaRating.tech.handle),
        TechSearchTerm(TechRatingSpec.phpRating.tech.handle)
      )), CompSpec.avitech) == NoMatchRank)
  }

  it should "match if fulltext matches but a tech does not" in new TestScope {
    assert(service.rank(CompSearchQuery(
      Set(
        TechSearchTerm(TechRatingSpec.phpRating.tech.handle),
        FulltextSearchTerm(CompSpec.avitech.name.toLowerCase)
      )), CompSpec.avitech) != NoMatchRank)
  }

  it should "match if fulltext matches but a city does not" in new TestScope {
    assert(service.rank(CompSearchQuery(
      Set(
        CitySearchTerm(CitySpec.noveZamky.handle),
        FulltextSearchTerm("aero")
      )), CompSpec.avitech) != NoMatchRank)
  }

  behavior of "compSearchQuery"

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

  behavior of "fulltextMatch"

  it should "match city name" in new TestScope {
    assert(service.fulltextMatch(Set(FulltextSearchTerm("bra")), CompSpec.avitech, 0) != NoMatchRank)
  }

  it should "match tech name" in new TestScope {
    assert(service.fulltextMatch(Set(FulltextSearchTerm("al")), CompSpec.avitech, 0) != NoMatchRank)
  }

  it should "match company name" in new TestScope {
    assert(service.fulltextMatch(Set(FulltextSearchTerm(CompSpec.avitech.name.toLowerCase)), CompSpec.avitech, 0) != NoMatchRank)
  }

  it should "match company name substring" in new TestScope {
    assert(service.fulltextMatch(Set(FulltextSearchTerm("mia")), CompSpec.avitech.copy(name = "BOHEMIA INTERACTIVE"), 0) != NoMatchRank)
  }

  it should "match company note" in new TestScope {
    assert(service.fulltextMatch(Set(FulltextSearchTerm("no")), CompSpec.avitech, 0) != NoMatchRank)
  }

  it should "match company URL" in new TestScope {
    assert(service.fulltextMatch(Set(FulltextSearchTerm("aero")), CompSpec.avitech, 0) != NoMatchRank)
  }

  it should "match company business number" in new TestScope {
    assert(service.fulltextMatch(Set(FulltextSearchTerm(CompSpec.avitech.businessNumber.value)), CompSpec.avitech, 0) != NoMatchRank)
  }

  behavior of "queryToTokens"

  it should "split raw query using regular expression" in new TestScope {
    assert(service.queryToTokens(" Scala  Bratislava,wTF    c++;pl/sql c#,,.NET") == Set("scala", "bratislava", "wtf", "c++", "pl/sql", "c#", ".net"))
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
