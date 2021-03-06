package sk.hrstka.repositories.itest

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Outcome, fixture}
import play.api.libs.json.Json
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection.JSONCollection
import sk.hrstka.itest.TestApplication
import sk.hrstka.repositories.mongoDb.MongoCollection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.reflect.ClassTag

abstract class BaseRepositoryISpec[TRepository : ClassTag](protected val testApplication: TestApplication, collection: MongoCollection)
  extends fixture.FlatSpec with ScalaFutures {

  override type FixtureParam = TRepository
  override protected def withFixture(test: OneArgTest): Outcome = {
    try {
      test.apply(testApplication.application.injector.instanceOf[TRepository])
    }
    finally {
      assert(testApplication.db.collection[JSONCollection](collection.name).remove(Json.obj()).futureValue.ok)
    }
  }
}