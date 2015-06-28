package sk.hrstka.models.domain

import sk.hrstka.models.db

object CompVoteSpec {
  val radosVotes = votesFor(db.UserSpec.rado)
  val johnysVotes = votesFor(db.UserSpec.johny)
  lazy val all = radosVotes ++ johnysVotes

  private def votesFor(user: db.User): Traversable[CompVote] =
    db.CompVoteSpec.all.filter(_.userId == user._id).map(VoteFactory(_))
}