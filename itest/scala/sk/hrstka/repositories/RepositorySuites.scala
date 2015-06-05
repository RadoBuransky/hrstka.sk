package sk.hrstka.repositories

import org.scalatest.{DoNotDiscover, Suites}
import sk.hrstka.itest.TestApplication

@DoNotDiscover
class StandaloneRepositorySuites extends Suites with TestApplication {
  override val nestedSuites = Vector(new RepositorySuites(this))
}

@DoNotDiscover
class RepositorySuites(testApplication: TestApplication) extends Suites {
  override val nestedSuites = Vector(
    new BaseMongoRepositoryISpec(testApplication),
    new MongoCityRepositoryISpec(testApplication),
    new MongoCompRepositoryISpec(testApplication),
    new MongoTechRepositoryISpec(testApplication),
    new MongoTechVoteRepositoryISpec(testApplication),
    new MongoUserRepositoryISpec(testApplication)
  )
}
