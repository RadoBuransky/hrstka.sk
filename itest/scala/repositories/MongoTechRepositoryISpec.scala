package repositories

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

  private def insertTechs(techRepository: TechRepository): Future[_] = {
    for {
      avitechFuture <- techRepository.upsert(scala)
      rescoFuture <- techRepository.upsert(java)
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