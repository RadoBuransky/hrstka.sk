import _root_.itest.TestApplication
import org.scalatest.Suites
import repositories._
import repositories.scripts.mongoDb.MongoDbManagerISpec

/**
 * Includes all integration tests that require running application.
 */
class ApplicationSuitesISpec extends Suites with TestApplication {
  override val nestedSuites = Vector(
    new RepositorySuitesISpec(this),
    new MongoDbManagerISpec(this)
  )
}
