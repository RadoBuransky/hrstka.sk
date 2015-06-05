package sk.hrstka.repositories

import org.scalatest.{DoNotDiscover, Suites}
import reactivemongo.bson.BSONObjectID
import sk.hrstka.itest.TestApplication
import sk.hrstka.models.db.{Identifiable, TechVote}
import sk.hrstka.repositories.itest.BaseRepositoryISpec
import sk.hrstka.repositories.mongoDb.{MongoTechVoteRepository, TechVoteCollection}

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
    val result = for {
      changed1 <- techVoteRepository.vote(scalaId, user1Id, 42)
      all1 <- techVoteRepository.all(Some(user1Id))
      changed2 <- techVoteRepository.vote(scalaId, user1Id, -13)
      all2 <- techVoteRepository.all(Some(user1Id))
    } yield (changed1, all1, changed2, all2)
    val (changed1, all1, changed2, all2) = result.futureValue
    assert(changed1)
    assert(emptyIds(all1).toSet ==
      Set(
        TechVote(
          _id     = Identifiable.empty,
          userId  = user1Id,
          techId  = scalaId,
          value   = 42
        )
      )
    )
    assert(changed2)
    assert(all2.toSet ==
      Set(
        TechVote(
          _id     = all1.head._id,
          userId  = user1Id,
          techId  = scalaId,
          value   = -13
        )
      )
    )
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
      user1Votes <- techVoteRepository.all(Some(user1Id))
      user2Votes <- techVoteRepository.all(Some(user2Id))
      user3Votes <- techVoteRepository.all(Some(BSONObjectID.generate))
    } yield (user1Votes, user2Votes, user3Votes)

    val (user1Votes, user2Votes, user3Votes) = result.futureValue
    assert(emptyIds(user1Votes).toSet ==
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

  it should "return all votes" in { techVoteRepository =>
    val result = for {
      _ <- techVoteRepository.vote(scalaId, user1Id, 1)
      _ <- techVoteRepository.vote(javaId, user1Id, 2)
      _ <- techVoteRepository.vote(javaId, user2Id, 3)
      allVotes <- techVoteRepository.all(None)
    } yield allVotes

    val allVotes = result.futureValue
    assert(emptyIds(allVotes).toSet ==
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
        ),
        TechVote(
          _id = Identifiable.empty,
          userId = user2Id,
          techId = javaId,
          value = 3
        )
      )
    )
  }

  private def emptyIds(techVotes: Traversable[TechVote]) = techVotes.map(emptyId)
  private def emptyId(techVote: TechVote) = techVote.copy(_id = Identifiable.empty)
}

object MongoTechVoteRepositoryISpec {
  val scalaId = BSONObjectID.generate
  val javaId = BSONObjectID.generate
  val user1Id = BSONObjectID.generate
  val user2Id = BSONObjectID.generate
}