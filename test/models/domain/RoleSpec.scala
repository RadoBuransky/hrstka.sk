package models.domain

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar

class RoleSpec extends FlatSpec with MockitoSugar {
  behavior of "Visitor"

  it should "be a Visitor" in { assert(Visitor.isA(Visitor)) }
  it should "not be an Eminent" in { assert(!Visitor.isA(Eminent)) }
  it should "not be an Admin" in { assert(!Visitor.isA(Admin)) }

  behavior of "Eminent"

  it should "be a Visitor" in { assert(Eminent.isA(Visitor)) }
  it should "be an Eminent" in { assert(Eminent.isA(Eminent)) }
  it should "not be an Admin" in { assert(!Eminent.isA(Admin)) }

  behavior of "Admin"

  it should "be a Visitor" in { assert(Admin.isA(Visitor)) }
  it should "be an Eminent" in { assert(Admin.isA(Eminent)) }
  it should "be an Admin" in { assert(Admin.isA(Admin)) }
}
