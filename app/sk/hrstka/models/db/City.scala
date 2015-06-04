package sk.hrstka.models.db

import sk.hrstka.models.db.Identifiable.{Handle, Id}

case class City(_id: Id,
                handle: Handle,
                sk: String) extends Identifiable
