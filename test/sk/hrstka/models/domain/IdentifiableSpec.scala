package sk.hrstka.models.domain

import reactivemongo.bson.BSONObjectID
import sk.hrstka.test.BaseSpec
import sk.hrstka.models.db

class IdentifiableSpec extends BaseSpec {
  behavior of "empty"

  it should "be empty" in {
    assert(Identifiable.empty.value.isEmpty)
  }

  behavior of "toBSON"

  it should "convert empty ID to empty BSON object ID" in {
    assert(Identifiable.toBSON(Identifiable.empty) == db.Identifiable.empty)
  }

  it should "convert nonempty ID to BSON object ID" in {
    val bson = BSONObjectID.generate
    assert(Identifiable.toBSON(Id(bson.stringify)) == bson)
  }

  behavior of "toDb"

  it should "convert handle to it's value" in {
    assert(Identifiable.toDb(Handle("x")) == "x")
  }

  behavior of "fromBSON"

  it should "convert empty BSON object ID to empty ID" in {
    assert(Identifiable.fromBSON(db.Identifiable.empty) == Identifiable.empty)
  }

  it should "convert nonempty BSON object ID to ID" in {
    val bson = BSONObjectID.generate
    assert(Identifiable.fromBSON(bson) == Id(bson.stringify))
  }
}
