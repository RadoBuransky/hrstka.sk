package sk.hrstka.models.ui

/**
 * Company rating.
 *
 * @param comp Company rated.
 * @param value Percentage between 0 and 100.
 */
case class CompRating(comp: Comp,
                      value: Int)

object CompRatingFactory {
  def apply(comp: Comp, compRatingValue: BigDecimal): CompRating =
    CompRating(comp, (compRatingValue.toDouble * 100.0).round.toInt)
}