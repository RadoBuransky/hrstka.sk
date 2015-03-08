package models.ui

import models.domain

case class Comp(id: String,
                name: String,
                website: String,
                rating: String,
                canVoteUp: Boolean,
                canVoteDown: Boolean,
                techs: Seq[Tech])

object Comp {
  def apply(comp: domain.Comp, canVoteUp: Boolean, canVoteDown: Boolean, techs: Seq[domain.Tech]) = new Comp(
    id          = comp.id,
    name        = comp.name,
    website     = comp.website.toString,
    rating      = "0",
    canVoteUp   = canVoteUp,
    canVoteDown = canVoteDown,
    techs       = techs.map(Tech(_, None)))
}