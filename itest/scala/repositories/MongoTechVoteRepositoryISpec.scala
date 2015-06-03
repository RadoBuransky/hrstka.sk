package repositories

import _root_.itest.TestApplication
import models.db.{Identifiable, TechVote}
import org.scalatest.{DoNotDiscover, Suites}
import reactivemongo.bson.BSONObjectID
import repositories.itest.BaseRepositoryISpec
import repositories.mongoDb.{MongoTechVoteRepository, TechVoteCollection}

import scala.concurrent.ExecutionContext.Implicits.global

@DoNotDiscover
class StandaloneMongoTechVoteRepositoryISpec extends Suites with TestApplication {
  override val nestedSuites = Vector(
    new MongoTechVoteRepositoryISpec(this)
  )
}

@DoNotDiscover
class MongoTechVoteRepositoryISpec(testApplication: TestApplication)
  extends BaseRepositoryISpec[MongoTechVoteRepository](testApplication, TechVoteCollection) {
  import MongoTechVoteRepositoryISpec._

  behavior of "vote"

  it should "create a new vote if does not exist yet" in { techVoteRepository =>
    val result = techVoteRepository.vote(scalaId, user1Id, 42).flatMap { changed =>
      assert(changed)
      techVoteRepository.findValue(scalaId, user1Id)
    }
    assert(result.futureValue.contains(42))
  }

  it should "update an existing vote" in { techVoteRepository =>
    val result = techVoteRepository.vote(scalaId, user1Id, 42).flatMap { changed1 =>
      assert(changed1)
      techVoteRepository.vote(scalaId, user1Id, -13).flatMap { changed2 =>
        assert(changed2)
        techVoteRepository.findValue(scalaId, user1Id)
      }
    }
    assert(result.futureValue.contains(-13))
  }

  it should "return false if value has not changed" in { techVoteRepository =>
    val result = techVoteRepository.vote(scalaId, user1Id, 42).flatMap { changed1 =>
      assert(changed1)
      techVoteRepository.vote(scalaId, user1Id, 42).flatMap { changed2 =>
        assert(!changed2)
        techVoteRepository.findValue(scalaId, user1Id)
      }
    }
    assert(result.futureValue.contains(42))
  }

  behavior of "all"

  it should "return all votes for users" in { techVoteRepository =>
    val result = for {
      _ <- techVoteRepository.vote(scalaId, user1Id, 1)
      _ <- techVoteRepository.vote(javaId, user1Id, 2)
      _ <- techVoteRepository.vote(javaId, user2Id, 3)
      user1Votes <- techVoteRepository.all(user1Id)
      user2Votes <- techVoteRepository.all(user2Id)
      user3Votes <- techVoteRepository.all(BSONObjectID.generate)
    } yield (user1Votes, user2Votes, user3Votes)

    val (user1Votes, user2Votes, user3Votes) = result.futureValue
    assert(user1Votes.map(_.copy(_id = Identifiable.empty)).toSet ==
      Set(
        TechVote(
        _id = Identifiable.empty,
        userId = user1Id,
        techId = scalaId,
        value = 1
        ),
        TechVote(
          _id = Identifiable.empty,
          userId = user1Id,
          techId = javaId,
          value = 2
        )))

    assert(user2Votes.map(_.copy(_id = Identifiable.empty)).toSet ==
      Set(
        TechVote(
          _id = Identifiable.empty,
          userId = user2Id,
          techId = javaId,
          value = 3
        )))

    assert(user3Votes.isEmpty)
  }
}

object MongoTechVoteRepositoryISpec {
  val scalaId = BSONObjectID.generate
  val javaId = BSONObjectID.generate
  val user1Id = BSONObjectID.generate
  val user2Id = BSONObjectID.generate
}