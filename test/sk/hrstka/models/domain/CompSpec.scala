package sk.hrstka.models.domain

import sk.hrstka.models
import sk.hrstka.models.domain
import sk.hrstka.test.BaseSpec

class CompSpec extends BaseSpec {
  import CompSpec._

  behavior of "constructor"

  it should "fail if name is empty" in {
    intercept[IllegalArgumentException] { avitech.copy(name = "\t  ") }
  }

  it should "fail if number of all employees is negative" in {
    intercept[IllegalArgumentException] { avitech.copy(employeeCount = Some(-1)) }
  }

  it should "fail if number of all employees is lower than number of all coders" in {
    intercept[IllegalArgumentException] { avitech.copy(employeeCount = Some(1)) }
  }

  it should "fail if number of coders is negative" in {
    intercept[IllegalArgumentException] { avitech.copy(codersCount = Some(-1)) }
  }

  it should "fail if number of coders is set but number of employees is not" in {
    intercept[IllegalArgumentException] { avitech.copy(employeeCount = None) }
  }

  it should "fail if number of all coders is lower than number of female coders" in {
    intercept[IllegalArgumentException] { avitech.copy(codersCount = Some(1)) }
  }

  it should "fail if number of female coders is negative" in {
    intercept[IllegalArgumentException] { avitech.copy(femaleCodersCount = Some(-1)) }
  }

  it should "fail if number of female coders is set but number of coders is not" in {
    intercept[IllegalArgumentException] { avitech.copy(codersCount = None) }
  }

  it should "fail if government business is negative" in {
    intercept[IllegalArgumentException] { avitech.copy(govBiz = Some(-1)) }
  }

  it should "fail if government business is more than 100" in {
    intercept[IllegalArgumentException] { avitech.copy(govBiz = Some(100.00001)) }
  }

  it should "fail if index of joel's question is negative" in {
    intercept[IllegalArgumentException] { avitech.copy(joel = Set(-1)) }
  }

  it should "fail if index of joel's question is more than 11" in {
    intercept[IllegalArgumentException] { avitech.copy(joel = Set(12)) }
  }
}

object CompSpec {
  val avitech = create(models.db.CompSpec.avitech)
  val borci = create(models.db.CompSpec.borci)
  lazy val all = Seq(avitech, borci)

  private def create(comp: models.db.Comp): domain.Comp =
    CompFactory(comp, comp.techs.map(TechRatingSpec.forHandle).toSeq.sortBy(-1 * _.value), CitySpec.forHandle(comp.city))
}