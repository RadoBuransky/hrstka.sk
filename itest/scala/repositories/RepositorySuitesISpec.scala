package repositories

import _root_.itest.TestApplication
import org.scalatest.Suites

class RepositorySuitesISpec extends Suites with TestApplication {
  override val nestedSuites = Vector(
    new MongoCompRepositoryISpec(this),
    new MongoTechRepositoryISpec(this)
  )
}
