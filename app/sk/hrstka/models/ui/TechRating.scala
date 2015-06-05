package sk.hrstka.models.ui

import sk.hrstka.models

/**
 * Technology rating.
 *
 * @param tech Technology rated.
 * @param value Percentage between 0 and 100.
 */
case class TechRating(tech: Tech,
                      value: Int)

object TechRatingFactory {
  def apply(techRating: models.domain.TechRating): TechRating =
    TechRating(TechFactory(techRating.tech), (techRating.value.toDouble * 100.0).round.toInt)
}
