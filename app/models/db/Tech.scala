package models.db

import models.db.Identifiable.Id

case class Tech(_id: Id,
                authorId: Id,
                name: String,
                upVotes: Int,
                downVotes: Int) extends Identifiable with Authorable