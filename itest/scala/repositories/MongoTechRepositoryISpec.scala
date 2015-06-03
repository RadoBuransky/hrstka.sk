package repositories

import _root_.itest.TestApplication
import common.HrstkaException
import models.db.{Identifiable, Tech}
import models.domain.Language
import org.scalatest.DoNotDiscover
import repositories.itest.BaseRepositoryISpec
import repositories.mongoDb.{MongoTechRepository, TechCollection}

import scala.concurrent.ExecutionContext.Implicits.global

@DoNotDiscover
class MongoTechRepositoryISpec(testApplication: TestApplication)
  extends BaseRepositoryISpec[MongoTechRepository](testApplication, TechCollection) {
  import MongoTechRepositoryISpec._

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

object MongoTechRepositoryISpec {
  val scala = Tech(
    _id             = Identifiable.empty,
    handle          = "scala",
    categoryHandle  = Language.handle.value,
    name            = "Scala",
    website         = "http://www.scala-lang.org/"
  )

  val java = Tech(
    _id             = Identifiable.empty,
    handle          = "java",
    categoryHandle  = Language.handle.value,
    name            = "Java",
    website         = "https://www.java.com/en/"
  )
}