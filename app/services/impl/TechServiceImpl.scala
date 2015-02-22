package services.impl

import common.HEException
import models._
import models.db.TechVote
import models.domain.Identifiable.{Id, _}
import models.domain.{Tech, TechRating}
import repositories.{TechRepository, TechVoteRepository}
import services.TechService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TechServiceImpl(techRepository: TechRepository,
                      techVoteRepository: TechVoteRepository) extends TechService {

  override def insert(name: String, userId: domain.Identifiable.Id) =
    techRepository.insert(
      name      = name.toLowerCase,
      authorId  = userId
    ).map(_.stringify)

  override def all() =
    techRepository.all().map(_.map(Tech(_)).sortBy(_.rating.map(-1 * _.value).getOrElse(0.0)))

  override def voteUp(id: Id, userId: Id) = vote(id, userId, 1, techVoteRepository.upVote, canVoteUp)
  override def voteDown(id: Id, userId: Id) = vote(id, userId, -1, techVoteRepository.downVote, canVoteDown)

  override def canVoteUp(id: Id, userId: Id) = canVote(id, userId, TechVote.downVoteValue)
  override def canVoteDown(id: Id, userId: Id) = canVote(id, userId, TechVote.upVoteValue)

  private def canVote(id: Id, userId: Id, value: Int) = techVoteRepository.latestVote(id, userId)
      .map(_.map(_.value == value)).map(_.getOrElse(true))

  private def vote(id: Id,
                   userId: Id,
                   delta: Int,
                   logVote: (db.Identifiable.Id, db.Identifiable.Id) => Future[Unit],
                   canVote: (Id, Id) => Future[Boolean]): Future[Unit] = {
    canVote(id, userId).map { can =>
      if (can) {
        val dbId = id
        val dbUserId = userId
        techRepository.updateRating(dbId, delta).map { Unit =>
          logVote(dbId, dbUserId)
        }
      }
      else
        throw new HEException(s"You cannot vote! [$id, $userId, $delta]")
    }
  }
}
