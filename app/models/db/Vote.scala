package models.db

import models.db.Identifiable.Id
import org.joda.time.DateTime

case class Vote(id: Id,
                authorId: Id,
                value: Int) extends Authorable