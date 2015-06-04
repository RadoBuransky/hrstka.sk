package sk.hrstka.repositories.scripts.mongoDb

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{DoNotDiscover, FlatSpec, Suites}
import reactivemongo.api.DBMetaCommands
import reactivemongo.api.indexes.NSIndex
import sk.hrstka.common.HrstkaException
import sk.hrstka.itest.TestApplication

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@DoNotDiscover
class StandaloneMongoDbManagerISpec extends Suites with TestApplication {
  override val nestedSuites = Vector(
    new MongoDbManagerISpec(this)
  )
}
@DoNotDiscover
class MongoDbManagerISpec(testApplication: TestApplication) extends FlatSpec with ScalaFutures {
  behavior of "applicationInit"

  it should "create compNameIndex" in { assert(indexExists(MongoDbManager.compNameIndex).futureValue) }
  it should "create compWebsiteIndex" in { assert(indexExists(MongoDbManager.compWebsiteIndex).futureValue) }
  it should "create techHandleIndex" in { assert(indexExists(MongoDbManager.techHandleIndex).futureValue) }
  it should "create userEmailIndex" in { assert(indexExists(MongoDbManager.userEmailIndex).futureValue) }
  it should "create cityHandleIndex" in { assert(indexExists(MongoDbManager.cityHandleIndex).futureValue) }
  it should "create techVoteUserTechIndex" in { assert(indexExists(MongoDbManager.techVoteUserTechIndex).futureValue) }

  private def indexExists(nsIndex: NSIndex): Future[Boolean] = {
    testApplication.db match {
      case dbMetaCommands: DBMetaCommands => dbMetaCommands.indexesManager.list().map { is =>
        is.exists { i =>
          i.index.copy(version = None, name = None) == nsIndex.index.copy(version = None, name = None)
        }
      }
      case _ => Future.failed(new HrstkaException("Not a DBMetaCommands instance!"))
    }
  }
}
