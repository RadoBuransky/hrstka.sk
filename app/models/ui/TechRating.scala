package models.ui

import models.domain

/**
 * Technology rating.
 *
 * @param tech Technology rated.
 * @param value Percentage between 0 and 100.
 */
case class TechRating(tech: Tech,
                      value: Double)

object TechRatingFactory {
  def apply(techRating: domain.TechRating): TechRating =
    TechRating(TechFactory(techRating.tech), techRating.value)
}
