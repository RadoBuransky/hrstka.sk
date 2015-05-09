package models.db

import models.db.Identifiable.{Id, Handle}

case class City(_id: Id,
                handle: Handle,
                sk: String) extends Identifiable
