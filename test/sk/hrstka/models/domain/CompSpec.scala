package sk.hrstka.models.domain

import sk.hrstka.models
import sk.hrstka.models.domain

object CompSpec {
  val avitech = create(models.db.CompSpec.avitech)
  val borci = create(models.db.CompSpec.borci)
  private def create(comp: models.db.Comp): domain.Comp =
    CompFactory(comp, comp.techs.map(TechRatingSpec.forHandle), CitySpec.forHandle(comp.city))
}