package sk.hrstka.services.impl

import org.mockito.ArgumentCaptor
import org.mockito.Matchers._
import org.mockito.Mockito._
import reactivemongo.bson.BSONObjectID
import sk.hrstka.common.HrstkaException
import sk.hrstka.models.db
import sk.hrstka.models.domain.Identifiable._
import sk.hrstka.models.domain._
import sk.hrstka.repositories.{CompRepository, TechRepository, TechVoteRepository}
import sk.hrstka.test.BaseSpec

import scala.concurrent.Future

class TechServiceImplSpec extends BaseSpec {
  behavior of "upsert"

  it should "map tech to DB and invoke repository" in new TestScope {
    // Prepare
    when(techRepository.upsert(any[db.Tech]))
      .thenReturn(Future.successful(BSONObjectID.generate))

    // Execute
    assert(techService.upsert(TechRatingSpec.akkaRating.tech).futureValue == TechRatingSpec.akkaRating.tech.handle)

    // Verify
    val techCaptor = ArgumentCaptor.forClass(classOf[db.Tech])
    verify(techRepository).upsert(techCaptor.capture())
    verifyNoMore()
    val tech = techCaptor.getValue

    // Assert
    assert(tech == db.TechSpec.akka)
  }

  behavior of "remove"

  it should "not allow to remove a technology if it is being used" in new TestScope {
    // Prepare
    when(compRepository.all())
      .thenReturn(Future.successful(Iterable(db.CompSpec.avitech)))

    // Execute
    whenReady(techService.remove(TechRatingSpec.scalaRating.tech.handle).failed) { ex =>
      ex.isInstanceOf[HrstkaException]
    }

    // Verify
    verify(compRepository).all()
  }

  it should "remove a technology if it not being used" in new TestScope {
    // Prepare
    when(compRepository.all())
      .thenReturn(Future.successful(Iterable.empty))
    when(techRepository.remove(db.TechSpec.scala.handle))
      .thenReturn(Future.successful(db.TechSpec.scala.handle))

    // Execute
    assert(techService.remove(TechRatingSpec.scalaRating.tech.handle).futureValue == TechRatingSpec.scalaRating.tech.handle)

    // Verify
    verify(techRepository).remove(db.TechSpec.scala.handle)
    verify(compRepository).all()
  }

  behavior of "getByHandle"

  it should "call repository and map to domain" in new TestScope {
    // Prepare
    when(techRepository.getByHandle(TechRatingSpec.scalaRating.tech.handle.value))
      .thenReturn(Future.successful(db.TechSpec.scala))

    // Execute
    assert(techService.getByHandle(TechRatingSpec.scalaRating.tech.handle).futureValue == TechRatingSpec.scalaRating.tech)

    // Verify
    verify(techRepository).getByHandle(TechRatingSpec.scalaRating.tech.handle.value)
    verifyNoMoreInteractions(techRepository)
  }

  behavior of "allRatings"

  it should "get all technology votes, all technologies and combine them" in new TestScope {
    // Prepare
    when(techVoteRepository.all(None))
      .thenReturn(Future.successful(db.TechVoteSpec.all))
    when(techRepository.all())
      .thenReturn(Future.successful(db.TechSpec.all))

    // Execute
    assertSeq(TechRatingSpec.allRatings, techService.allRatings().futureValue)

    // Verify
    verify(techVoteRepository).all(None)
    verify(techRepository).all()
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

  behavior of "votesFor"

  it should "return all votes for the user" in new TestScope {
    // Prepare
    when(techVoteRepository.all(Some(db.UserSpec.rado._id)))
      .thenReturn(Future.successful(db.TechVoteSpec.all.filter(_.userId == db.UserSpec.rado._id)))

    // Execute
    assertSet(TechVoteSpec.radosVotes.toSet, techService.votesFor(db.UserSpec.rado._id).futureValue.toSet)

    // Verify
    verify(techVoteRepository).all(Some(db.UserSpec.rado._id))
    verifyNoMore()
  }

  behavior of "allCategories"

  it should "return hardcoded categories" in new TestScope {
    assert(techService.allCategories().futureValue == TechCategory.allCategories)
    verifyNoMore()
  }

  private class VoteTestScope extends TestScope {
    def testVoteUp(original: Option[Int], expected: Int): Unit = testVote(original, expected, techService.voteUp)
    def testVoteDown(original: Option[Int], expected: Int): Unit = testVote(original, expected, techService.voteDown)

    private def testVote(original: Option[Int], expected: Int, f: (Handle, Id) => Future[Unit]): Unit = {
      // Prepare
      when(techRepository.getByHandle(db.TechSpec.php.handle))
        .thenReturn(Future.successful(db.TechSpec.php))
      when(techVoteRepository.findValue(db.TechSpec.php._id, db.UserSpec.rado._id))
        .thenReturn(Future.successful(original))

      // Execute
      whenReady(f(TechRatingSpec.phpRating.tech.handle, db.UserSpec.rado._id)) { _ =>
        // Verify
        verify(techRepository).getByHandle(db.TechSpec.php.handle)
        verify(techVoteRepository).findValue(db.TechSpec.php._id, db.UserSpec.rado._id)
        if (!original.contains(expected))
          verify(techVoteRepository).vote(db.TechSpec.php._id, db.UserSpec.rado._id, expected)
        verifyNoMore()
      }
    }
  }

  private class TestScope {
    val techRepository = mock[TechRepository]
    val techVoteRepository = mock[TechVoteRepository]
    val compRepository = mock[CompRepository]
    val techService = new TechServiceImpl(techRepository, techVoteRepository, compRepository)

    def verifyNoMore(): Unit = {
      verifyNoMoreInteractions(techRepository)
      verifyNoMoreInteractions(techVoteRepository)
      verifyNoMoreInteractions(compRepository)
    }
  }
}
