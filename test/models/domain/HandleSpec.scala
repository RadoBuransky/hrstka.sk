package models.domain

import test.BaseSpec

class HandleSpec extends BaseSpec {
  behavior of "fromHumanName"

  it should "replace diacritics in slovak text" in {
    assert(HandleFactory.fromHumanName("Liptovský Mikuláš").value === "liptovsky-mikulas")
  }
}
