package repositories

import itest.TestApplication
import models.db.{Identifiable, Tech}
import org.scalatest.DoNotDiscover
import reactivemongo.bson.BSONObjectID
import repositories.mongoDb.TechCollection

import scala.concurrent.ExecutionContext.Implicits.global

@DoNotDiscover
class MongoTechRepositoryISpec(testApplication: TestApplication)
  extends BaseRepositoryISpec[TechRepository](testApplication, TechCollection) {
  behavior of "insert"

  it should "insert a new technology" in { techRepository =>
    val authorId = BSONObjectID.generate
    val result = techRepository.insert("scala", authorId).flatMap { techId =>
      techRepository.get(techId).map(techId -> _)
    }
    val (techId: Identifiable.Id, tech) = result.futureValue
    assert(tech == Tech(
      _id           = techId,
      authorId      = authorId,
      handle        = "scala",
      upVotes       = 0,
      upVotesValue  = 0,
      downVotes     = 0
    ))
  }
}
