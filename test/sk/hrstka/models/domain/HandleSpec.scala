package sk.hrstka.models.domain

import sk.hrstka.test.BaseSpec

class HandleSpec extends BaseSpec {
  behavior of "fromHumanName"

  it should "replace diacritics in slovak text" in {
    assert(HandleFactory.fromHumanName("Liptovský Mikuláš").value === "liptovsky-mikulas")
  }
}
