package models.domain

import common.HEException

/**
 * Technology rating. It is a ratio of pluses and minuses.
 *
 * @param value Percentage of plus votes.
 */
case class TechRating(value: Double) {
  if (value > 100.0)
    throw new HEException(s"Tech rating cannot be greater than 100 [$value]")
  if (value < 0.0)
    throw new HEException(s"Tech rating cannot be less than 0 [$value]")
}

object TechRating {
  def apply(upVotes: Int, downVotes: Int): Option[TechRating] = {
    val count = upVotes + downVotes
    if (count == 0)
      None
    else
      Some(TechRating(upVotes.toDouble / count.toDouble))
  }
}