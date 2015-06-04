package sk.hrstka.models.domain

import sk.hrstka.test.BaseSpec

class CompRatingSpec extends BaseSpec {
  import CompRatingSpec._

  behavior of "constructor"

  it should "fail if value is greater than 1.0" in {
    intercept[IllegalArgumentException](CompRating(CompSpec.avitech, 1.000001))
  }

  it should "fail if value is lower than 0.0" in {
    intercept[IllegalArgumentException](CompRating(CompSpec.avitech, -0.000001))
  }

  behavior of "techRating"

  it should "return 0.0 if there are no tech ratings" in {
    assert(CompRatingFactory.techRating(CompSpec.avitech.copy(techRatings = Set.empty)) == 0.0)
  }

  it should "return average of all tech rating values" in {
    assert(CompRatingFactory.techRating(CompSpec.avitech) == 0.75)
  }

  behavior of "joelsTest"

  it should "return ratio of checked points" in {
    assert(CompRatingFactory.joelsTest(CompSpec.avitech) == 0.25)
  }

  behavior of "femaleRatio"

  it should "return 0.0 if there are no female programmers" in {
    assert(CompRatingFactory.femaleRatio(CompSpec.avitech.copy(femaleCodersCount = None)) == 0.0)
  }

  it should "return 0.0 if there are no male programmers" in {
    assert(CompRatingFactory.femaleRatio(CompSpec.avitech.copy(codersCount = None, femaleCodersCount = None)) == 0.0)
  }

  it should "return 1.0 if number of male programmers equals number of female programmers" in {
    assert(CompRatingFactory.femaleRatio(CompSpec.avitech.copy(codersCount = Some(60), femaleCodersCount = Some(30))) == 1.0)
  }

  it should "return 0.0 if all programmers are male" in {
    assert(CompRatingFactory.femaleRatio(CompSpec.avitech.copy(codersCount = Some(60), femaleCodersCount = Some(0))) == 0.0)
  }

  it should "return 0.0 if all programmers are female" in {
    assert(CompRatingFactory.femaleRatio(CompSpec.avitech.copy(codersCount = Some(60), femaleCodersCount = Some(60))) == 0.0)
  }

  it should "return ratio" in {
    assert(CompRatingFactory.femaleRatio(CompSpec.avitech) == 1.0)
  }

  behavior of "codersRatio"

  it should "return 0.0 if there are no employees" in {
    assert(CompRatingFactory.codersRatio(CompSpec.avitech.copy(employeeCount = None, codersCount = None, femaleCodersCount = None)) == 0.0)
  }

  it should "return 0.0 if there are no programmers" in {
    assert(CompRatingFactory.codersRatio(CompSpec.avitech.copy(codersCount = None, femaleCodersCount = None)) == 0.0)
  }

  it should "return 1.0 if all employees are programmers" in {
    assert(CompRatingFactory.codersRatio(CompSpec.avitech.copy(employeeCount = Some(60), codersCount = Some(60))) == 1.0)
  }

  it should "return 0.5 if half of employees are programmers" in {
    assert(CompRatingFactory.codersRatio(CompSpec.avitech.copy(employeeCount = Some(60), codersCount = Some(30))) == 0.5)
  }

  behavior of "apply"

  it should "use 70% weight for tech rating" in {
    assert(CompRatingFactory(zeroRatedAvitech.copy(techRatings = Set(TechSpec.scalaRating))).value == 0.7)
  }

  it should "use 10% weight for Joel's test" in {
    assert(CompRatingFactory(zeroRatedAvitech.copy(joel = Set(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11))).value == 0.1)
  }

  it should "use 10% weight for female programmers ratio" in {
    assert(CompRatingFactory(zeroRatedAvitech.copy(employeeCount = Some(2), codersCount = Some(2), femaleCodersCount = Some(1))).value == 0.2)
  }

  it should "use 10% weight for programmers ratio" in {
    assert(CompRatingFactory(zeroRatedAvitech.copy(employeeCount = Some(2), codersCount = Some(2))).value == 0.1)
  }

  it should "return 100% if everything is at best" in {
    assert(CompRatingFactory(zeroRatedAvitech.copy(
      techRatings       = Set(TechSpec.scalaRating),
      joel              = Set(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
      employeeCount     = Some(2),
      codersCount       = Some(2),
      femaleCodersCount = Some(1))).value.round == 1)
  }

  it should "return 0% if everything is at worst" in {
    assert(CompRatingFactory(zeroRatedAvitech).value == 0.0)
  }
}

object CompRatingSpec {
  val zeroRatedAvitech = CompSpec.avitech.copy(
    techRatings       = Set.empty,
    joel              = Set.empty,
    employeeCount     = None,
    codersCount       = None,
    femaleCodersCount = None
  )
}