package itest

import common.HEException
import org.scalatest.{BeforeAndAfterAll, Suite, SuiteMixin}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.{Application, Logger, Mode}
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.{DB, DBMetaCommands}
import repositories.scripts.DbManager

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

trait TestApplication extends SuiteMixin with BeforeAndAfterAll {
  this: Suite =>
  private val logger: Logger = Logger(getClass)

  private var _application: Application = _
  private var _db: DB = _

  def application = _application
  def db = _db

  override protected def beforeAll() = {
    _application = new GuiceApplicationBuilder()
      .in(Mode.Test)
      .configure("mongodb.db" -> "hrstka-itest")
      .build()
    _db = application.injector.instanceOf[ReactiveMongoApi].db
    Await.result(prepareDb(db), 30.seconds)
  }

  override protected def afterAll() = {
    // Drop DB ...
    val closing = dropDb(db).flatMap { _ =>
      // ... and close connection
      db.connection.askClose()(30.seconds)
    }
    Await.result(closing, 30.seconds)
  }

  private def prepareDb(db: DB): Future[_] = application.injector.instanceOf[DbManager].applicationInit()

  private def dropDb(db: DB): Future[_] = {
    db match {
      case dbMetaCommands: DBMetaCommands => {
        dbMetaCommands.drop().map { _ =>
          logger.info(s"Testing DB dropped. [${db.name}]")
        }
      }
      case _ => Future.failed(new HEException("No a DBMetaCommands instance!"))
    }
  }
}