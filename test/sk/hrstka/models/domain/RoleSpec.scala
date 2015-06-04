package sk.hrstka.models.domain

import sk.hrstka.test.BaseSpec

class RoleSpec extends BaseSpec {
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
