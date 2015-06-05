package sk.hrstka.models.domain

/**
 * Technology rating.
 *
 * @param tech Technology rated.
 * @param value Number value between 0.0 and 1.0. The more the better.
 */
case class TechRating(tech: Tech,
                      value: BigDecimal) {
  if (value > 1.0)
    throw new IllegalArgumentException(s"Tech rating cannot be greater than 1.0 [$value]")
  if (value < 0.0)
    throw new IllegalArgumentException(s"Tech rating cannot be less than 0.0 [$value]")
}

object TechRatingFactory {
  val maxVoteValue = 3
  val minVoteValue = -1

  /**
   * Computes tech rating as a ratio of sum of all positive votes and number of all votes multiplied by value of
   * maximal possible positive vote. E.g. the result is 1.0 if everyone who votes gives full points.
   *
   * @param tech Tech to compute rating for.
   * @param upVotesValue Sum of all values of positive votes. Negatives are not counted.
   * @param voteCount Number of all votes other than zero. Zero is not a vote.
   * @return Tech rating value.
   */
  def apply(tech: Tech, upVotesValue: Int, voteCount: Int): TechRating =
    if (voteCount == 0)
      TechRating(tech, 0)
    else {
      if (upVotesValue > voteCount * maxVoteValue)
        throw new IllegalArgumentException(s"Illegal vote value! [${tech.handle}, $upVotesValue, $voteCount]")
      TechRating(tech, upVotesValue.toDouble / (voteCount.toDouble * maxVoteValue.toDouble))
    }
}