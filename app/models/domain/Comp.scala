package models.domain

import java.net.URL

import models.domain.Identifiable.Id

case class Comp(id: Id,
                authorId: Id,
                name: String,
                website: URL) extends Identifiable with Authorable
