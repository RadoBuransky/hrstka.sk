package models.domain

import models.db

object CompSpec {
  val avitech = create(db.CompSpec.avitech)
  val borci = create(db.CompSpec.borci)
  private def create(comp: db.Comp): Comp =
    CompFactory(comp, comp.techs.map(TechSpec.forHandle), CitySpec.forHandle(comp.city))
}