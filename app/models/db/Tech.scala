package models.db

import models.db.Identifiable.{Handle, Id}

/**
 * Technology.
 * @param _id Identifier.
 * @param authorId User identifier.
 * @param handle Human-friendly identifier.
 * @param upVotes Number of users with positive vote.
 * @param upVotesValue Total value of positive votes.
 * @param downVotes Number of users with negative vote.
 */
case class Tech(_id: Id,
                authorId: Id,
                handle: Handle,
                upVotes: Int,
                upVotesValue: Int,
                downVotes: Int) extends Identifiable with Authorable