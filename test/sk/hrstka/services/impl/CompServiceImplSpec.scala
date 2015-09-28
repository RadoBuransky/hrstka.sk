package sk.hrstka.services.impl

import org.mockito.ArgumentCaptor
import org.mockito.Matchers._
import org.mockito.Mockito._
import reactivemongo.bson.BSONObjectID
import sk.hrstka.models.domain._
import sk.hrstka.models.{db, domain}
import sk.hrstka.repositories.{CompRepository, CompVoteRepository}
import sk.hrstka.services.{CompSearchService, LocationService, TechService}
import sk.hrstka.test.BaseSpec

import scala.concurrent.Future

class CompServiceImplSpec extends BaseSpec {
  import CompSpec._

  behavior of "upsert"

  it should "map user to DB and invoke repository" in new TestScope {
    // Prepare
    val userId = BSONObjectID.generate.stringify
    val compId = BSONObjectID.generate
    when(compRepository.upsert(any[db.Comp])).thenReturn(Future.successful(compId))

    // Execute
    assert(compService.upsert(
      comp        = avitech,
      techHandles = avitech.techRatings.map(_.tech.handle).toSet,
      userId      = Identifiable.fromBSON(db.CompSpec.avitech.authorId)
    ).futureValue == avitech.businessNumber)

    // Verify
    val compCaptor = ArgumentCaptor.forClass(classOf[db.Comp])
    verify(compRepository).upsert(compCaptor.capture())
    verifyNoMore()
    val comp = compCaptor.getValue

    // Assert
    assert(comp == db.CompSpec.avitech)
  }

  behavior of "all"

//  it should "return all companies sorted by rating if no city or tech is provided" in new TestScope {
//    // Prepare
//    when(compRepository.all(None, None))
//      .thenReturn(Future.successful(Seq(db.CompSpec.avitech, db.CompSpec.borci)))
//    when(techService.allRatings())
//      .thenReturn(Future.successful(TechRatingSpec.allRatings))
//    when(locationService.city(Handle(db.CitySpec.bratislava.handle)))
//      .thenReturn(Future.successful(CitySpec.bratislava))
//    when(locationService.city(Handle(db.CitySpec.noveZamky.handle)))
//      .thenReturn(Future.successful(CitySpec.noveZamky))
//    when(compVoteRepository.all(None))
//      .thenReturn(Future.successful(db.CompVoteSpec.all))
//
//    // Execute
//    val result = compService.all(None, None).futureValue
//    assertCompRating(CompRatingSpec.avitech, result.find(_.comp.id == avitech.id).get)
//    assertCompRating(CompRatingSpec.borci, result.find(_.comp.id == borci.id).get)
//    assertResult(Seq(CompRatingSpec.avitech, CompRatingSpec.borci))(result)
//
//    // Verify
//    verify(compVoteRepository).all(None)
//    verify(compRepository).all(None, None)
//    verify(techService).allRatings()
//    verify(locationService).city(Handle(db.CitySpec.bratislava.handle))
//    verify(locationService).city(Handle(db.CitySpec.noveZamky.handle))
//    verifyNoMore()
//  }
//
//  it should "return all companies in Bratislava" in new TestScope {
//    // Prepare
//    when(compRepository.all(city = Some(db.CitySpec.bratislava.handle), None))
//      .thenReturn(Future.successful(Seq(db.CompSpec.avitech)))
//    when(techService.allRatings())
//      .thenReturn(Future.successful(TechRatingSpec.allRatings))
//    when(locationService.city(Handle(db.CitySpec.bratislava.handle)))
//      .thenReturn(Future.successful(CitySpec.bratislava))
//    when(compVoteRepository.all(None))
//      .thenReturn(Future.successful(db.CompVoteSpec.all))
//
//    // Execute
//    val result = futureValue(compService.all(city = Some(CitySpec.bratislava.handle), None)).toSet
//    assertCompRating(CompRatingSpec.avitech, result.find(_.comp.id == avitech.id).get)
//    assertResult(Set(CompRatingSpec.avitech))(result)
//
//    // Verify
//    verify(compVoteRepository).all(None)
//    verify(compRepository).all(city = Some(db.CitySpec.bratislava.handle), None)
//    verify(techService).allRatings()
//    verify(locationService).city(Handle(db.CitySpec.bratislava.handle))
//    verifyNoMore()
//  }
//
//  it should "return all companies that use PHP" in new TestScope {
//    // Prepare
//    when(compRepository.all(None, tech = Some(TechRatingSpec.phpRating.tech.handle.value)))
//      .thenReturn(Future.successful(Seq(db.CompSpec.borci)))
//    when(techService.allRatings())
//      .thenReturn(Future.successful(TechRatingSpec.allRatings))
//    when(locationService.city(Handle(db.CitySpec.noveZamky.handle)))
//      .thenReturn(Future.successful(CitySpec.noveZamky))
//    when(compVoteRepository.all(None))
//      .thenReturn(Future.successful(db.CompVoteSpec.all))
//
//    // Execute
//    val result = futureValue(compService.all(None, tech = Some(TechRatingSpec.phpRating.tech.handle))).toSet
//    assertCompRating(CompRatingSpec.borci, result.find(_.comp.id == borci.id).get)
//    assertResult(Set(CompRatingSpec.borci))(result)
//
//    // Verify
//    verify(compVoteRepository).all(None)
//    verify(compRepository).all(None, tech = Some(TechRatingSpec.phpRating.tech.handle.value))
//    verify(techService).allRatings()
//    verify(locationService).city(Handle(db.CitySpec.noveZamky.handle))
//    verifyNoMore()
//  }
//
//  it should "return all companies in Bratislava that use PHP" in new TestScope {
//    // Prepare
//    when(compRepository.all(city = Some(db.CitySpec.bratislava.handle), tech = Some(TechRatingSpec.phpRating.tech.handle.value)))
//      .thenReturn(Future.successful(Seq.empty))
//    when(techService.allRatings())
//      .thenReturn(Future.successful(TechRatingSpec.allRatings))
//    when(compVoteRepository.all(None))
//      .thenReturn(Future.successful(db.CompVoteSpec.all))
//
//    // Execute
//    val result = futureValue(compService.all(city = Some(CitySpec.bratislava.handle), tech = Some(TechRatingSpec.phpRating.tech.handle))).toSet
//    assertResult(Set.empty)(result)
//
//    // Verify
//    verify(compVoteRepository).all(None)
//    verify(compRepository).all(city = Some(db.CitySpec.bratislava.handle), tech = Some(TechRatingSpec.phpRating.tech.handle.value))
//    verify(techService).allRatings()
//    verifyNoMore()
//  }

  behavior of "get"

  it should "return a company" in new TestScope {
    // Prepare
    when(techService.allRatings())
      .thenReturn(Future.successful(TechRatingSpec.allRatings))
    when(compRepository.get(db.CompSpec.avitech.businessNumber))
      .thenReturn(Future.successful(db.CompSpec.avitech))
    when(locationService.city(Handle(db.CitySpec.bratislava.handle)))
      .thenReturn(Future.successful(CitySpec.bratislava))

    // Execute
    val result = futureValue(compService.get(avitech.businessNumber))
    assertResult(avitech)(result)

    // Verify
    verify(techService).allRatings()
    verify(compRepository).get(db.CompSpec.avitech.businessNumber)
    verify(locationService).city(Handle(db.CitySpec.bratislava.handle))
    verifyNoMore()
  }

  behavior of "topWomen"

//  it should "return sorted list of companies with the most women programmers" in new TestScope {
//    val noCodersSetComp = db.CompSpec.avitech.copy(codersCount = None, femaleCodersCount = None)
//    val noCodersComp = db.CompSpec.avitech.copy(employeeCount = None, codersCount = None, femaleCodersCount = None)
//    val noFemaleCodersSetComp = db.CompSpec.avitech.copy(femaleCodersCount = None)
//    val noFemaleCodersComp = db.CompSpec.avitech.copy(femaleCodersCount = Some(0))
//
//    // Prepare
//    when(compRepository.all(None, None))
//      .thenReturn(Future.successful(Seq(
//        db.CompSpec.borci,
//        noCodersSetComp,
//        noCodersComp,
//        db.CompSpec.avitech,
//        noFemaleCodersSetComp,
//        noFemaleCodersComp)))
//    when(techService.allRatings())
//      .thenReturn(Future.successful(TechRatingSpec.allRatings))
//    when(locationService.city(Handle(db.CitySpec.bratislava.handle)))
//      .thenReturn(Future.successful(CitySpec.bratislava))
//    when(locationService.city(Handle(db.CitySpec.noveZamky.handle)))
//      .thenReturn(Future.successful(CitySpec.noveZamky))
//    when(compVoteRepository.all(None))
//      .thenReturn(Future.successful(db.CompVoteSpec.all))
//
//    // Execute
//    assertResult(Seq(CompRatingSpec.borci, CompRatingSpec.avitech)) {
//      futureValue(compService.topWomen())
//    }
//
//    // Verify
//    verify(compVoteRepository).all(None)
//    verify(compRepository).all(None, None)
//    verify(techService).allRatings()
//    verify(locationService, times(5)).city(Handle(db.CitySpec.bratislava.handle))
//    verify(locationService).city(Handle(db.CitySpec.noveZamky.handle))
//    verifyNoMore()
//  }

  behavior of "voteFor"

  it should "return votes for the given user and company" in new TestScope {
    // Prepare
    when(compRepository.get(CompSpec.avitech.businessNumber.value))
      .thenReturn(Future.successful(db.CompSpec.avitech))
    when(compVoteRepository.findValue(db.CompSpec.avitech._id, db.UserSpec.rado._id))
      .thenReturn(Future.successful(Some(db.CompVoteSpec.avitechRado.value)))

    // Execute
    assertResult(Some(CompVote(CompSpec.avitech.id, UserSpec.rado.id, db.CompVoteSpec.avitechRado.value))) {
      futureValue(compService.voteFor(CompSpec.avitech.businessNumber, UserSpec.rado.id))
    }

    // Verify
    verify(compRepository).get(CompSpec.avitech.businessNumber.value)
    verify(compVoteRepository).findValue(db.CompSpec.avitech._id, db.UserSpec.rado._id)
    verifyNoMore()
  }

  behavior of "voteUp"

  it should "set vote value to 1 if no vote exists yet" in new VoteTestScope { testVoteUp(None, 1) }
  it should "set vote value to 0 if was -1" in new VoteTestScope { testVoteUp(Some(-1), 0) }
  it should "set vote value to 1 if was 0" in new VoteTestScope { testVoteUp(Some(0), 1) }
  it should "set vote value to 2 if was 1" in new VoteTestScope { testVoteUp(Some(1), 2) }
  it should "set vote value to 3 if was 2" in new VoteTestScope { testVoteUp(Some(2), 3) }
  it should "not change vote value if was 3" in new VoteTestScope { testVoteUp(Some(3), 3) }

  behavior of "voteDown"

  it should "set vote value to -1 if no vote exists yet" in new VoteTestScope { testVoteDown(None, -1) }
  it should "set vote value to 2 if was 3" in new VoteTestScope { testVoteDown(Some(3), 2) }
  it should "set vote value to 1 if was 2" in new VoteTestScope { testVoteDown(Some(2), 1) }
  it should "set vote value to 0 if was 1" in new VoteTestScope { testVoteDown(Some(1), 0) }
  it should "set vote value to -1 if was 0" in new VoteTestScope { testVoteDown(Some(0), -1) }

  behavior of "search"

  it should "orchestrate everything" in new TestScope {
    when(techService.allRatings()).thenReturn(Future.successful(TechRatingSpec.allRatings))
    when(compRepository.all(None, None))
      .thenReturn(Future.successful(db.CompSpec.all))
    when(compVoteRepository.all(None))
      .thenReturn(Future.successful(db.CompVoteSpec.all))
    when(locationService.city(Handle(db.CitySpec.bratislava.handle)))
      .thenReturn(Future.successful(CitySpec.bratislava))
    when(locationService.city(Handle(db.CitySpec.noveZamky.handle)))
      .thenReturn(Future.successful(CitySpec.noveZamky))
    when(compSearchService.rank(any(), any()))
      .thenReturn(MatchedRank(1.0))

    val result = futureValue(compService.search(CompSearchQuery(Set.empty)))
    assertResult(Seq(CompRatingSpec.avitech, CompRatingSpec.borci))(result)

    verify(compSearchService, times(2)).rank(any(), any())
    verify(locationService).city(Handle(db.CitySpec.noveZamky.handle))
    verify(locationService).city(Handle(db.CitySpec.bratislava.handle))
    verify(compVoteRepository).all(None)
    verify(compRepository).all(None, None)
    verify(techService).allRatings()
    verifyNoMore()
  }

  behavior of "filterAndSort"

  it should "remove companies with no match" in new TestScope {
    val compRanks = Seq(
      CompRatingSpec.avitech -> MatchedRank(0.5),
      CompRatingSpec.borci -> NoMatchRank
    )
    assert(compService.filterAndSort(compRanks) == Seq(CompRatingSpec.avitech))
  }

  it should "sort companies first by search rank and then by vote value" in new TestScope {
    val comp1 = CompSpec.avitech.copy(name = "comp1")
    val comp2 = CompSpec.avitech.copy(name = "comp2")
    val comp3 = CompSpec.borci.copy(name = "comp3")
    val comp4 = CompSpec.borci.copy(name = "comp4")

    val compRating1 = CompRating(comp1, 0.7)
    val compRating2 = CompRating(comp2, 0.1)
    val compRating3 = CompRating(comp3, 0.8)
    val compRating4 = CompRating(comp4, 0.2)

    val compRanks = Seq(
      compRating1 -> MatchedRank(0.4),
      compRating2 -> MatchedRank(0.5),
      compRating3 -> MatchedRank(0.4),
      compRating4 -> MatchedRank(0.5)
    )

    val result = compService.filterAndSort(compRanks)
    assert(result.size == 4)
    assertResult(compRating4.comp.name)(result.head.comp.name)
    assertResult(compRating2.comp.name)(result(1).comp.name)
    assertResult(compRating3.comp.name)(result(2).comp.name)
    assertResult(compRating1.comp.name)(result(3).comp.name)
  }

  private def assertCompRating(expected: domain.CompRating, actual: domain.CompRating): Unit = {
    assertComp(expected.comp, actual.comp)
    assert(expected.value == actual.value, expected.comp.name)
  }

  private def assertComp(expected: domain.Comp, actual: domain.Comp): Unit = {
    // Partial assertions
    assertUnapplied(
      domain.Comp.unapply(expected).get.productIterator.toSeq,
      domain.Comp.unapply(actual).get.productIterator.toSeq)

    // Double-check
    assertResult(expected)(actual)
  }

  private def assertUnapplied(expected: Seq[Any], actual: Seq[Any]): Unit =
    expected.zip(actual).foreach { pair =>
      assertResult(pair._1)(pair._2)
    }

  private class VoteTestScope extends TestScope {
    def testVoteUp(original: Option[Int], expected: Int): Unit = testVote(original, expected, compService.voteUp)
    def testVoteDown(original: Option[Int], expected: Int): Unit = testVote(original, expected, compService.voteDown)

    private def testVote(original: Option[Int], expected: Int, f: (BusinessNumber, Id) => Future[Unit]): Unit = {
      // Prepare
      when(compRepository.get(CompSpec.avitech.businessNumber.value))
        .thenReturn(Future.successful(db.CompSpec.avitech))
      when(compVoteRepository.findValue(db.CompSpec.avitech._id, db.UserSpec.rado._id))
        .thenReturn(Future.successful(original))

      // Execute
      whenReady(f(CompSpec.avitech.businessNumber, UserSpec.rado.id)) { _ =>
        // Verify
        verify(compRepository).get(CompSpec.avitech.businessNumber.value)
        verify(compVoteRepository).findValue(db.CompSpec.avitech._id, db.UserSpec.rado._id)
        if (!original.contains(expected))
          verify(compVoteRepository).vote(db.CompSpec.avitech._id, db.UserSpec.rado._id, expected)
        verifyNoMore()
      }
    }
  }

  private class TestScope {
    val compRepository = mock[CompRepository]
    val compVoteRepository = mock[CompVoteRepository]
    val techService = mock[TechService]
    val locationService = mock[LocationService]
    val compSearchService = mock[CompSearchService]
    val compService = new CompServiceImpl(
      compRepository,
      compVoteRepository,
      techService,
      locationService,
      compSearchService)

    def verifyNoMore(): Unit = {
      verifyNoMoreInteractions(compRepository)
      verifyNoMoreInteractions(techService)
      verifyNoMoreInteractions(locationService)
    }
  }
}
