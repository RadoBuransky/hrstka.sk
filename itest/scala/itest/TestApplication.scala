package itest

import org.scalatest.{BeforeAndAfterAll, Suite, SuiteMixin}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.{Application, Logger, Mode}
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.DBMetaCommands

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.io.Source

trait TestApplication extends SuiteMixin with BeforeAndAfterAll {
  this: Suite =>
  private val logger: Logger = Logger("TestApplication")
  private var _application: Application = _

  protected def application = _application

  override protected def beforeAll() = {
    _application = new GuiceApplicationBuilder()
      .in(Mode.Test)
      .configure("mongodb.db" -> "hrstka-itest")
      .build()

    Await.result(prepareDb(application.injector.instanceOf[ReactiveMongoApi]), 30.seconds)
  }

  override protected def afterAll() = {
    Await.result(dropDb(application.injector.instanceOf[ReactiveMongoApi]), 30.seconds)
  }

  private def prepareDb(reactiveMongoApi: ReactiveMongoApi): Future[_] = {
    reactiveMongoApi.db match {
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

  private def dropDb(reactiveMongoApi: ReactiveMongoApi): Future[_] = {
    reactiveMongoApi.db match {
      case dbMetaCommands: DBMetaCommands => dbMetaCommands.drop()
      case _ => Future.successful(())
    }
  }
}
