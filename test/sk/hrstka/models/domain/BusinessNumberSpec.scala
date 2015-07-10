package sk.hrstka.models.domain

import sk.hrstka.test.BaseSpec

class BusinessNumberSpec extends BaseSpec {
  behavior of "constructor"

  it should "fail if business number is empty" in {
    intercept[IllegalArgumentException](BusinessNumber("\t "))
  }

  behavior of "SlovakBusinessNumber"

  it should "fail if length is 5 or 7 or 9" in {
    intercept[IllegalArgumentException](SlovakBusinessNumber("12345"))
    intercept[IllegalArgumentException](SlovakBusinessNumber("1234567"))
    intercept[IllegalArgumentException](SlovakBusinessNumber("123456789"))
  }

  it should "be ok if length is 6 or 8" in {
    SlovakBusinessNumber("123456")
    SlovakBusinessNumber("35887401")
  }

  it should "fail if it contains anything else but digits" in {
    intercept[IllegalArgumentException](SlovakBusinessNumber("12s456-8"))
  }
}
