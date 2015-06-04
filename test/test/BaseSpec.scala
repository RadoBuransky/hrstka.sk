package test

import org.scalatest.FlatSpec
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatest.time.{Millis, Seconds, Span}
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Common trait for all unit tests.
 */
trait BaseSpec extends FlatSpec with MockitoSugar with ScalaFutures {
  protected val logger = LoggerFactory.getLogger("SpecLogger")

  implicit override val patienceConfig = PatienceConfig(timeout = Span(2, Seconds), interval = Span(5, Millis))

  protected def futureValue[T](f: Future[T]): T = {
    f.onFailure {
      case ex => logger.error("Future execution failed!", ex)
    }
    f.futureValue
  }
}
