package sk.hrstka.models.domain

import sk.hrstka.common.HrstkaException

/**
 * Company rating.
 *
 * @param comp Company.
 * @param value Number value between 0.0 and 1.0. The more the better.
 */
case class CompRating(comp: Comp,
                      value: Double) {
  if (value > 1.0)
    throw new HrstkaException(s"Company rating cannot be greater than 1 [$value]")
  if (value < 0.0)
    throw new HrstkaException(s"Company rating cannot be less than 0 [$value]")
}

object CompRatingFactory {
  // TODO: Unit test
  /**
   * Computes company rating:
   *   70% technology ratings
   *     - average of all technology rating values
   *   10% Joel's test
   *     - percentage of how many points checked
   *   10% ratio of female / male programmers
   *     - the close to 1:1 the better rating
   *   10% ratio of programmers / all employees
   *     - full score if all employees are programmers, this a tech website
   *
   * @param comp Company to compute rating for.
   * @return Company rating.
   */
  def apply(comp: Comp): CompRating = {
    CompRating(
      comp,
      0.7 * techRating(comp) +
      0.1 * joelsTest(comp) +
      0.1 * femaleRatio(comp) +
      0.1 * codersRatio(comp))
  }

  private def techRating(comp: Comp): Double = {
    val techRatingValues = comp.techRatings.map(_.value)
    if (techRatingValues.isEmpty)
      0.0
    else
      techRatingValues.sum / techRatingValues.size.toDouble
  }

  private def joelsTest(comp: Comp): Double = comp.joel.size / 12.0

  private def femaleRatio(comp: Comp): Double = {
    val females = comp.femaleCodersCount.getOrElse(0)
    val males = comp.codersCount.getOrElse(0) - females

    if (females == 0 || males == 0)
      0.0
    else {
      val opt = (females + males).toDouble / 2.0
      1.0 - (Math.abs(females - opt) / opt)
    }
  }

  private def codersRatio(comp: Comp): Double = {
    val all = comp.employeeCount.getOrElse(0)
    val coders = comp.codersCount.getOrElse(0)

    if (all == 0 || coders == 0)
      0.0
    else
      coders.toDouble / all.toDouble
  }
}