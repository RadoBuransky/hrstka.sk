package models.db

import models.db.Identifiable._

case class VoteLog(_id: Id,
                   id: Id,
                   authorId: Id,
                   value: Int) extends Identifiable