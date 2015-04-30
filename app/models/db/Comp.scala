package models.db

import models.db.Identifiable.Id

case class Comp(_id: Id,
                authorId: Id,
                name: String,
                website: String,
                location: String,
                employeeCount: Option[Int],
                codersCount: Option[Int],
                femaleCodersCount: Option[Int],
                note: String,
                products: Boolean,
                services: Boolean,
                internal: Boolean,
                techs: Seq[Id],
                joel: Set[Int]) extends Identifiable with Authorable
