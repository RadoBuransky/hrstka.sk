package sk.hrstka.repositories

import _root_.itest.TestApplication
import common.HrstkaException
import org.scalatest.DoNotDiscover
import sk.hrstka.models.db.TechSpec
import sk.hrstka.repositories.itest.BaseRepositoryISpec
import sk.hrstka.repositories.mongoDb.{MongoTechRepository, TechCollection}

import scala.concurrent.ExecutionContext.Implicits.global

@DoNotDiscover
class MongoTechRepositoryISpec(testApplication: TestApplication)
  extends BaseRepositoryISpec[MongoTechRepository](testApplication, TechCollection) {
  import TechSpec._

  behavior of "upsert"

  it should "not allow to insert a tech with the same handle" in { techRepository =>
    val result = for {
      insertedScala1 <-techRepository.upsert(scala)
      insertedScala2 <-techRepository.upsert(java.copy(handle = scala.handle))
    } yield insertedScala2

    whenReady(result.failed) { ex =>
      assert(ex.isInstanceOf[HrstkaException])
    }
  }
}