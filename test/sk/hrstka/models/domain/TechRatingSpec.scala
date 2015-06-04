package sk.hrstka.models.domain

import sk.hrstka.test.BaseSpec

class TechRatingSpec extends BaseSpec {
  behavior of "constructor"

  it should "fail if value is greater than 1.0" in {
    intercept[IllegalArgumentException](TechRating(TechSpec.scalaRating.tech, 1.000001))
  }

  it should "fail if value is lower than 0.0" in {
    intercept[IllegalArgumentException](TechRating(TechSpec.scalaRating.tech, -0.000001))
  }

  behavior of "factory"

  it should "return 0.0 in case of no votes" in {
    assert(TechRatingFactory(TechSpec.scalaRating.tech, 0, 0).value == 0.0)
  }

  it should "return 1.0 if all votes have max value" in {
    assert(TechRatingFactory(TechSpec.scalaRating.tech, 5 * TechRatingFactory.maxVoteValue, 5).value == 1.0)
  }

  it should "return 0.0 if all votes are negative" in {
    assert(TechRatingFactory(TechSpec.scalaRating.tech, 0, 5).value == 0.0)
  }

  it should "return 0.5 for 1 and 2" in {
    assert(TechRatingFactory(TechSpec.scalaRating.tech, 1 + 2, 2).value == 0.5)
  }

  it should "return 0.5 for 3 and -1" in {
    assert(TechRatingFactory(TechSpec.scalaRating.tech, 3, 2).value == 0.5)
  }
}
