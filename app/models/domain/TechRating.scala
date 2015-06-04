package models.domain

import common.HrstkaException

/**
 * Technology rating.
 *
 * @param tech Technology rated.
 * @param value Percentage between 0 and 100.
 */
case class TechRating(tech: Tech,
                      value: Double) {
  if (value > 100.0)
    throw new HrstkaException(s"Tech rating cannot be greater than 100 [$value]")
  if (value < 0.0)
    throw new HrstkaException(s"Tech rating cannot be less than 0 [$value]")
}

object TechRatingFactory {
  val maxVoteValue = 3
  val minVoteValue = -1

  def apply(tech: Tech, upVotesValue: Int, voteCount: Int): TechRating =
    if (voteCount == 0)
      TechRating(tech, 0)
    else
      TechRating(tech, (upVotesValue.toDouble * 100.0) / (voteCount.toDouble * maxVoteValue.toDouble))
}