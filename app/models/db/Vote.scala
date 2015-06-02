package models.db

import models.db.Identifiable.Id
import org.joda.time.DateTime

case class Vote(_id: Id,
                id: Id,
                authorId: Id,
                value: Int) extends Identifiable with Authorable