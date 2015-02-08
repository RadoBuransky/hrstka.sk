package models.ui

import models._

case class Tech(id: String,
                name: String,
                rating: String,
                canVoteUp: Boolean,
                canVoteDown: Boolean)

object Tech {
  def apply(tech: domain.Tech, canVoteUp: Boolean, canVoteDown: Boolean) = new Tech(
    id          = tech.id,
    name        = tech.name,
    rating      = "%1.0f" format tech.rating.map(_.value * 100.0).getOrElse(0.0),
    canVoteUp   = canVoteUp,
    canVoteDown = canVoteDown)
}