package models.domain

import java.net.URL

import models.domain.Identifiable.Id
import models.db

case class Comp(id: Id,
                name: String,
                website: URL,
                location: String,
                codersCount: Option[Int],
                femaleCodersCount: Option[Int],
                note: String) extends Identifiable

object Comp {
  def apply(comp: db.Comp): Comp = Comp(
    id                = comp._id.stringify,
    name              = comp.name,
    website           = new URL(comp.website),
    location          = comp.location,
    codersCount       = comp.codersCount,
    femaleCodersCount = comp.femaleCodersCount,
    note              = comp.note
  )
}