package models.db

import reactivemongo.bson.BSONObjectID

case class Tech(_id: Option[BSONObjectID],
                author: BSONObjectID,
                name: String,
                rating: Option[Double]) extends Identifiable with Authorable