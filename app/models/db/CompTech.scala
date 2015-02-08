package models.db

import models.db.Identifiable.Id
import org.joda.time.DateTime

case class CompTech(_id: Id,
                    authorId: Id,
                    compId: Id,
                    techId: Id,
                    index: Int,
                    removed: Option[DateTime]) extends Identifiable with Authorable
