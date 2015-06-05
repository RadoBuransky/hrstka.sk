package sk.hrstka.models.domain

import sk.hrstka.models
import sk.hrstka.models.domain
import sk.hrstka.test.BaseSpec

class CompSpec extends BaseSpec {
  import CompSpec._

  behavior of "constructor"

  it should "fail if number of all employees is lower than number of all coders" in {
    intercept[IllegalArgumentException] { avitech.copy(employeeCount = Some(1)) }
  }

  it should "fail if number of coders is set but number of employees is not" in {
    intercept[IllegalArgumentException] { avitech.copy(employeeCount = None) }
  }

  it should "fail if number of all coders is lower than number of female coders" in {
    intercept[IllegalArgumentException] { avitech.copy(codersCount = Some(1)) }
  }

  it should "fail if number of female coders is set but number of coders is not" in {
    intercept[IllegalArgumentException] { avitech.copy(codersCount = None) }
  }
}

object CompSpec {
  val avitech = create(models.db.CompSpec.avitech)
  val borci = create(models.db.CompSpec.borci)
  private def create(comp: models.db.Comp): domain.Comp =
    CompFactory(comp, comp.techs.map(TechRatingSpec.forHandle), CitySpec.forHandle(comp.city))
}