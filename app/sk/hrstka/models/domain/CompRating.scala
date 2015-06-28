package sk.hrstka.models.domain

/**
 * Company rating.
 *
 * @param comp Company.
 * @param value Number value between 0.0 and 1.0. The more the better.
 */
case class CompRating(comp: Comp,
                      value: BigDecimal) {
  if (value > 1.0)
    throw new IllegalArgumentException(s"Company rating cannot be greater than 1 [$value]")
  if (value < 0.0)
    throw new IllegalArgumentException(s"Company rating cannot be less than 0 [$value]")
}

object CompRatingFactory {
  val maxVoteValue = 3
  val minVoteValue = -1

  /**
   * Computes company rating:
   *   30% technology ratings
   *     - average of all technology rating values
   *   30% eminents' votes
   *   20% percentage of business with government in the last year's revenue
   *   10% Joel's test
   *     - percentage of how many points checked
   *   10% ratio of female / male programmers
   *     - the closer to 1:1 the better rating
   *
   * @param comp Company to compute rating for.
   * @return Company rating.
   */
  def apply(comp: Comp, upVotesValue: Int, voteCount: Int): CompRating = {
    CompRating(
      comp,
      0.3 * techRating(comp) +
      0.3 * compVotes(upVotesValue, voteCount) +
      0.2 * govBiz(comp) +
      0.1 * joelsTest(comp) +
      0.1 * femaleRatio(comp))
  }

  private[domain] def techRating(comp: Comp): BigDecimal = {
    val techRatingValues = comp.techRatings.map(_.value)
    if (techRatingValues.isEmpty)
      0.0
    else
      techRatingValues.sum / techRatingValues.size
  }

  private[domain] def compVotes(upVotesValue: Int, voteCount: Int): BigDecimal = {
    if (voteCount == 0)
      0
    else {
      if (upVotesValue > voteCount * maxVoteValue)
        throw new IllegalArgumentException(s"Illegal vote value! [$upVotesValue, $voteCount]")
      upVotesValue.toDouble / (voteCount.toDouble * maxVoteValue.toDouble)
    }
  }

  private[domain] def govBiz(comp: Comp): BigDecimal = comp.govBiz.map(n => (100 - n) / 100.0).getOrElse(0)

  private[domain] def joelsTest(comp: Comp): BigDecimal = comp.joel.size / 12.0

  private[domain] def femaleRatio(comp: Comp): BigDecimal = {
    val females = comp.femaleCodersCount.getOrElse(0)
    val males = comp.codersCount.getOrElse(0) - females

    if (females == 0 || males == 0)
      0.0
    else {
      val opt = (females + males) / 2.0
      1.0 - (Math.abs(females - opt) / opt)
    }
  }
}