package repositories

import common.HEException
import itest.TestApplication
import models.db.{Identifiable, Tech}
import org.scalatest.DoNotDiscover
import reactivemongo.bson.BSONObjectID
import repositories.mongoDb.TechCollection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@DoNotDiscover
class MongoTechRepositoryISpec(testApplication: TestApplication)
  extends BaseRepositoryISpec[TechRepository](testApplication, TechCollection) {
  import MongoTechRepositoryISpec._
  behavior of "upsert"

  it should "insert a new technology" in { techRepository =>
    val result = techRepository.upsert(scala.copy(_id = Identifiable.empty)).flatMap { techId =>
      techRepository.get(techId)
    }
    val insertedScala = result.futureValue
    assert(insertedScala == scala.copy(_id = insertedScala._id))
  }

  behavior of "all"

  it should "return everything" in { techRepository =>
    val result = insertTechs(techRepository).flatMap { _ =>
      techRepository.all()
    }
    assert(result.futureValue.toSet == Set(scala, java))
  }

  behavior of "updateRating"

  it should "fail if delta is 0" in { techRepository =>
    intercept[IllegalArgumentException] { techRepository.updateRating(scala._id, 0, 0) }
  }

  it should "fail if delta is -2" in { techRepository =>
    intercept[IllegalArgumentException] { techRepository.updateRating(scala._id, -2, 0) }
  }

  it should "fail if delta is 2" in { techRepository =>
    intercept[IllegalArgumentException] { techRepository.updateRating(scala._id, 2, 0) }
  }

  it should "increase scala rating" in { techRepository =>
    val result = for {
      insertedScala <- techRepository.upsert(scala)
      increasedScala <- techRepository.updateRating(scala._id, 1, 1)
      updatedScala <- techRepository.get(scala._id)
    } yield updatedScala

    val updatedScala = result.futureValue
    assert(updatedScala == scala.copy(upVotes = 1, upVotesValue = 1))
  }

  it should "decrease scala rating" in { techRepository =>
    val result = for {
      insertedScala <- techRepository.upsert(scala)
      increasedScala <- techRepository.updateRating(scala._id, -1, -1)
      updatedScala <- techRepository.get(scala._id)
    } yield updatedScala

    val updatedScala = result.futureValue
    assert(updatedScala == scala.copy(downVotes = 1))
  }

  behavior of "get"

  it should "return existing technology" in { techRepository =>
    val result = insertTechs(techRepository).flatMap { _ =>
      techRepository.get(scala._id)
    }
    assert(result.futureValue == scala)
  }

  it should "fail for nonexisting technology" in { techRepository =>
    val result = insertTechs(techRepository).flatMap { _ =>
      techRepository.get(BSONObjectID.generate)
    }
    whenReady(result.failed) { ex =>
      assert(ex.isInstanceOf[HEException])
    }
  }

  private def insertTechs(techRepository: TechRepository): Future[_] = {
    for {
      scalaFuture <- techRepository.upsert(scala)
      javaFuture <- techRepository.upsert(java)
    } yield ()
  }
}

object MongoTechRepositoryISpec {
  val scala = Tech(
    _id           = BSONObjectID.generate,
    authorId      = BSONObjectID.generate,
    handle        = "scala",
    upVotes       = 0,
    upVotesValue  = 0,
    downVotes     = 0
  )

  val java = Tech(
    _id           = BSONObjectID.generate,
    authorId      = BSONObjectID.generate,
    handle        = "java",
    upVotes       = 5,
    upVotesValue  = 7,
    downVotes     = 3
  )
}