package models.db

import models.db.Identifiable.Id

case class Comp(_id: Id,
                authorId: Id,
                name: String,
                website: String,
                location: String,
                codersCount: Option[Int],
                femaleCodersCount: Option[Int],
                note: String) extends Identifiable with Authorable
