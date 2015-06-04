import _root_.itest.TestApplication
import org.scalatest.Suites
import sk.hrstka.repositories._
import sk.hrstka.repositories.scripts.mongoDb.MongoDbManagerISpec

/**
 * Includes all integration tests that require running application.
 */
class ApplicationSuites extends Suites with TestApplication {
  override val nestedSuites = Vector(
    new MongoDbManagerISpec(this)
  ) ++ new RepositorySuites(this).nestedSuites
}