package models.domain

import models.domain.Identifiable.Id
import models.{domain, db}

case class Tech(id: Id,
                authorId: Id,
                name: String,
                rating: Option[TechRating]) extends Identifiable with Authorable

object Tech {
  def apply(src: db.Tech): Tech = {
    domain.Tech(
      id = src._id.stringify,
      authorId = src.authorId.stringify,
      name = src.name,
      rating = TechRating(src.upVotes,src.downVotes))
  }
}