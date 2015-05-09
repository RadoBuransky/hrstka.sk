package models.domain

import org.scalatest.FlatSpec

class HandleSpec extends FlatSpec {
  behavior of "fromHumanName"

  it should "replace diacritics in slovak text" in {
    assert(Handle.fromHumanName("Liptovský Mikuláš").value === "liptovsky-mikulas")
  }
}
