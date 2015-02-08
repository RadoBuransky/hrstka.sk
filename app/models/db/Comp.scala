package models.db

import models.db.Identifiable.Id

case class Comp(_id: Id,
                authorId: Id,
                name: String,
                website: String,
                upVotes: Int,
                downVotes: Int) extends Identifiable with Authorable
