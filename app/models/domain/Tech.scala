package models.domain

import models.domain.Identifiable.Id

/**
 * Technology
 *
 * @param id Identifier.
 * @param author Creator.
 * @param name Tech name.
 * @param rating Overall rating.
 */
case class Tech(id: Id,
                author: Id,
                name: String,
                rating: Option[TechRating]) extends Identifiable with Authorable

