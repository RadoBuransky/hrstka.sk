package models.ui

import models._

case class Tech(id: String,
                name: String,
                rating: String,
                userVoteValue: Option[Int])

object Tech {
  def apply(tech: domain.Tech, userVoteValue: Option[Int]) = new Tech(
    id            = tech.id,
    name          = tech.name,
    rating        = "%1.0f" format tech.rating.map(_.value * 100.0).getOrElse(0.0),
    userVoteValue = userVoteValue)
}