package repositories

import itest.TestApplication
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Outcome, fixture}
import reactivemongo.core.commands.Drop
import repositories.mongoDb.MongoCollection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.reflect.ClassTag

abstract class BaseRepositoryISpec[TRepository : ClassTag](collection: MongoCollection)
  extends fixture.FlatSpec with TestApplication with ScalaFutures {

  override type FixtureParam = TRepository
  override protected def withFixture(test: OneArgTest): Outcome = {
    try {
      test.apply(application.injector.instanceOf[TRepository])
    }
    finally {
      assert(db.command(new Drop(collection.name)).futureValue)
    }
  }
}
