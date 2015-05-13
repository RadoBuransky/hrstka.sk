package models.ui

import models._

case class Tech(id: String,
                handle: String,
                rating: String,
                userVoteValue: Option[Int])

object Tech {
  def apply(tech: domain.Tech, userVoteValue: Option[Int] = None) = new Tech(
    id            = tech.id,
    handle          = tech.handle.value,
    rating        = "%1.0f" format Math.abs(tech.rating.value * 100.0),
    userVoteValue = userVoteValue)
}