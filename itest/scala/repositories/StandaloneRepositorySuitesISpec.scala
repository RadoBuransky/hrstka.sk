package repositories

import _root_.itest.TestApplication
import org.scalatest.{DoNotDiscover, Suites}

@DoNotDiscover
class StandaloneRepositorySuitesISpec extends Suites with TestApplication {
  override val nestedSuites = Vector(new RepositorySuitesISpec(this))
}

@DoNotDiscover
class RepositorySuitesISpec(testApplication: TestApplication) extends Suites {
  override val nestedSuites = Vector(
    new BaseMongoRepositoryISpec(testApplication),
    new MongoCityRepositoryISpec(testApplication),
    new MongoCompRepositoryISpec(testApplication),
    new MongoTechRepositoryISpec(testApplication),
    new MongoTechVoteRepositoryISpec(testApplication),
    new MongoUserRepositoryISpec(testApplication)
  )
}
