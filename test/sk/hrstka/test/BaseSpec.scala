package sk.hrstka.test

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{FlatSpec, Matchers}
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Common trait for all unit tests.
 */
trait BaseSpec extends FlatSpec with MockitoSugar with ScalaFutures with Matchers {
  protected val logger = LoggerFactory.getLogger("SpecLogger")

  implicit override val patienceConfig = PatienceConfig(timeout = Span(2, Seconds), interval = Span(5, Millis))

  protected def assertSeq[T](expected: Seq[T], actual: Seq[T]): Unit = {
    // Assert content
    assertSet(expected.toSet, actual.toSet)

    // Assert order
    val swapped = expected.zipWithIndex.map(ei => ei._2 -> actual.indexOf(ei._1)).filter(ij => ij._1 != ij._2)
    if (swapped.nonEmpty)
      fail(s"Contents match, but order not: $swapped")
  }

  protected def assertSet[T](expected: Set[T], actual: Set[T]): Unit = {
    val missing = expected.diff(actual)
    val notExpected = actual.diff(expected)
    if (missing.nonEmpty || notExpected.nonEmpty) {
      fail(s"Missing (${missing.size}):\r\n${missing.mkString("\r\n")}\r\nNot expected (${notExpected.size}):\r\n${notExpected.mkString("\r\n")}")
    }
  }

  protected def futureValue[T](f: Future[T]): T = {
    f.onFailure {
      case ex => logger.error("Future execution failed!", ex)
    }
    f.futureValue
  }
}
