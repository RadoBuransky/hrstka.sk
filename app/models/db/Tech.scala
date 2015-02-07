package models.db

import models.db.Identifiable.Id

case class Tech(_id: Option[Id],
                author: Id,
                name: String,
                upVotes: Int,
                downVotes: Int) extends Identifiable with Authorable