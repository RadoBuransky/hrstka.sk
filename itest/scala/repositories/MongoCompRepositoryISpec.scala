package repositories

import java.net.URL

import itest.TestApplication
import models.db.Comp
import org.scalatest.DoNotDiscover
import reactivemongo.bson.BSONObjectID
import repositories.mongoDb.{CompCollection, MongoCompRepository}
import scala.concurrent.ExecutionContext.Implicits.global

@DoNotDiscover
class MongoCompRepositoryISpec(testApplication: TestApplication)
  extends BaseRepositoryISpec[MongoCompRepository](testApplication, CompCollection) {
  import MongoCompRepositoryISpec._

  behavior of "get"

  it should "get inserted company" in { compRepository =>
    val result = compRepository.upsert(avitech).flatMap { _ =>
      compRepository.get(avitech._id)
    }
    assert(result.futureValue == avitech)
  }
}

object MongoCompRepositoryISpec {
  lazy val avitech = Comp(
    _id               = BSONObjectID.generate,
    authorId          = BSONObjectID.generate,
    name              = "Avitech",
    website           = new URL("http://avitech.aero/").toString,
    city              = "avitech",
    employeeCount     = Some(60),
    codersCount       = Some(30),
    femaleCodersCount = Some(5),
    note              = "note",
    products          = true,
    services          = true,
    internal          = false,
    techs             = Seq("scala", "java"),
    joel              = Set(3, 5, 7)
  )
}