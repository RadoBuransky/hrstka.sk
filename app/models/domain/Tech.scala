package models.domain

import models.domain.Identifiable.Id
import models.{db, domain}

case class Tech(id: Id,
                authorId: Id,
                name: String,
                rating: TechRating) extends Identifiable with Authorable

object Tech {
  def apply(src: db.Tech): Tech = {
    domain.Tech(
      id = src._id.stringify,
      authorId = src.authorId.stringify,
      name = src.name,
      rating = TechRating(src.upVotesValue, src.downVotes + src.upVotes))
  }
}