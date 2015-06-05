package sk.hrstka.models.domain

import sk.hrstka.models
import sk.hrstka.test.BaseSpec

class TechRatingSpec extends BaseSpec {
  import TechRatingSpec._

  behavior of "constructor"

  it should "fail if value is greater than 1.0" in {
    intercept[IllegalArgumentException](TechRating(scalaRating.tech, 1.000001))
  }

  it should "fail if value is lower than 0.0" in {
    intercept[IllegalArgumentException](TechRating(scalaRating.tech, -0.000001))
  }

  behavior of "factory"

  it should "return 0.0 in case of no votes" in {
    assert(TechRatingFactory(scalaRating.tech, 0, 0).value == BigDecimal(0))
  }

  it should "return 1.0 if all votes have max value" in {
    assert(TechRatingFactory(scalaRating.tech, 5 * TechRatingFactory.maxVoteValue, 5).value == BigDecimal(1))
  }

  it should "return 0.0 if all votes are negative" in {
    assert(TechRatingFactory(scalaRating.tech, 0, 5).value == BigDecimal(0))
  }

  it should "return 0.5 for 1 and 2" in {
    assert(TechRatingFactory(scalaRating.tech, 1 + 2, 2).value == BigDecimal(0.5))
  }

  it should "return 0.5 for 3 and -1" in {
    assert(TechRatingFactory(scalaRating.tech, 3, 2).value == BigDecimal(0.5))
  }

  it should "return 1 for Scala (3)" in {
    assert(TechRatingFactory(scalaRating.tech, 3, 1).value == scalaRating.value)
  }

  it should "return 0.5 for Java (3, -1)" in {
    assert(TechRatingFactory(javaRating.tech, 3, 2).value == javaRating.value)
  }

  it should "return 0 for PHP (-1)" in {
    assert(TechRatingFactory(phpRating.tech, 0, 1).value == phpRating.value)
  }

  it should "return 0.8333333333333334 for Akka (2, 3)" in {
    assert(TechRatingFactory(akkaRating.tech, 2 + 3, 2).value == akkaRating.value)
  }

  it should "return 0.3333333333333333 for Apache (-1, 2)" in {
    assert(TechRatingFactory(apacheRating.tech, 2, 2).value == apacheRating.value)
  }

  it should "fail if up votes value is unrealistically high" in {
    intercept[IllegalArgumentException] { TechRatingFactory(akkaRating.tech, 666, 1) }
  }
}

object TechRatingSpec {
  val scalaRating = TechRating(TechFactory(models.db.TechSpec.scala), 1.0)
  val akkaRating = TechRating(TechFactory(models.db.TechSpec.akka), 0.8333333333333334)
  val javaRating = TechRating(TechFactory(models.db.TechSpec.java), 0.5)
  val apacheRating = TechRating(TechFactory(models.db.TechSpec.apache), 0.3333333333333333)
  val phpRating = TechRating(TechFactory(models.db.TechSpec.php), 0.0)
  lazy val allRatings = Seq(scalaRating, javaRating, phpRating, akkaRating, apacheRating).sortBy(-1 * _.value)
  def forHandle(handle: String): TechRating = allRatings.find(_.tech.handle.value == handle).get
}