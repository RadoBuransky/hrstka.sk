package itest

import org.scalatest.{BeforeAndAfterAll, Suite, SuiteMixin}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.{Application, Logger, Mode}
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.{DB, DBMetaCommands}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.io.Source

trait TestApplication extends SuiteMixin with BeforeAndAfterAll {
  this: Suite =>
  private val logger: Logger = Logger("TestApplication")

  private var _application: Application = _
  private var _db: DB = _

  protected def application = _application
  protected def db = _db

  override protected def beforeAll() = {
    _application = new GuiceApplicationBuilder()
      .in(Mode.Test)
      .configure("mongodb.db" -> "hrstka-itest")
      .build()

    _db = application.injector.instanceOf[ReactiveMongoApi].db

    Await.result(prepareDb(db), 30.seconds)
  }

  override protected def afterAll() = {
    Await.result(dropDb(db), 30.seconds)
  }

  private def prepareDb(db: DB): Future[_] = {
    db match {
      case dbMetaCommands: DBMetaCommands => dbMetaCommands.eval(readDbInitScript(), nolock = false)
      case _ => Future.successful(())
    }
  }

  private def readDbInitScript(): String = {
    val fileStream = getClass.getResourceAsStream("/db_init.js")
    if (fileStream == null)
      throw new RuntimeException("db_init.js not found!")
    Source.fromInputStream(fileStream).mkString
  }

  private def dropDb(db: DB): Future[_] = {
    db match {
      case dbMetaCommands: DBMetaCommands => dbMetaCommands.drop()
      case _ => Future.successful(())
    }
  }
}
