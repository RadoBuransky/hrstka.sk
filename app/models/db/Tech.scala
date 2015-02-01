package models.db

import models.db.Identifiable.Id

case class Tech(_id: Option[Id],
                author: Id,
                name: String,
                rating: Option[Double]) extends Identifiable with Authorable