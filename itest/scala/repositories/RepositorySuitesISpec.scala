package repositories

import itest.TestApplication
import org.scalatest.Suites

class RepositorySuitesISpec extends Suites with TestApplication {
  override val nestedSuites = Vector(
    new MongoCityRepositoryISpec(this),
    new MongoCompRepositoryISpec(this),
    new MongoTechRepositoryISpec(this)
  )
}
