package models.db

import models.db.Identifiable.{Handle, Id}

case class Tech(_id: Id,
                authorId: Id,
                handle: Handle,
                upVotes: Int,
                upVotesValue: Int,
                downVotes: Int) extends Identifiable with Authorable