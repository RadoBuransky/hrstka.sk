package repositories

import itest.TestApplication
import models.db.Tech
import models.domain.Language
import org.scalatest.DoNotDiscover
import reactivemongo.bson.BSONObjectID
import repositories.mongoDb.TechCollection

@DoNotDiscover
class MongoTechRepositoryISpec(testApplication: TestApplication)
  extends BaseRepositoryISpec[TechRepository](testApplication, TechCollection) {
}

object MongoTechRepositoryISpec {
  val scala = Tech(
    _id             = BSONObjectID.generate,
    handle          = "scala",
    categoryHandle  = Language.handle.value,
    name            = "Scala",
    website         = "http://www.scala-lang.org/"
  )

  val java = Tech(
    _id             = BSONObjectID.generate,
    handle          = "java",
    categoryHandle  = Language.handle.value,
    name            = "Java",
    website         = "https://www.java.com/en/"
  )
}