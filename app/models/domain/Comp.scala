package models.domain

import java.net.URL

import models.domain.Identifiable.Id

case class Comp(id: Id,
                name: String,
                website: URL,
                location: String,
                codersCount: Option[Int],
                femaleCodersCount: Option[Int],
                note: String) extends Identifiable