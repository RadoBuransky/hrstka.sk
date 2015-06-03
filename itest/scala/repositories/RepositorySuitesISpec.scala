package repositories

import _root_.itest.TestApplication
import org.scalatest.Suites

class RepositorySuitesISpec extends Suites with TestApplication {
  override val nestedSuites = Vector(
    new BaseMongoRepositoryISpec(this),
    new MongoCityRepositoryISpec(this),
    new MongoCompRepositoryISpec(this),
    new MongoTechRepositoryISpec(this),
    new MongoUserRepositoryISpec(this)
  )
}
