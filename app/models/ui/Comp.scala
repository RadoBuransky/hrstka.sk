package models.ui

import models.domain

case class Comp(id: String,
                name: String,
                website: String,
                location: String,
                codersCount: Option[Int],
                femaleCodersCount: Option[Int],
                note: String)

object Comp {
  def apply(comp: domain.Comp) = new Comp(
    id                = comp.id,
    name              = comp.name,
    website           = comp.website.toString,
    location          = comp.location,
    codersCount       = comp.codersCount,
    femaleCodersCount = comp.femaleCodersCount,
    note              = comp.note)
}