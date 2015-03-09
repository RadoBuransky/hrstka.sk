package models.domain

import common.HEException

/**
 * Technology rating.
 */
case class TechRating(value: Double) {
  if (value > 100.0)
    throw new HEException(s"Tech rating cannot be greater than 100 [$value]")
  if (value < 0.0)
    throw new HEException(s"Tech rating cannot be less than 0 [$value]")
}

object TechRating {
  val maxVoteValue = 3
  val minVoteValue = -1

  def apply(upVotesValue: Int, voteCount: Int): TechRating =
    if (voteCount == 0)
      TechRating(0)
    else
      TechRating(upVotesValue.toDouble / (voteCount.toDouble * maxVoteValue))
}