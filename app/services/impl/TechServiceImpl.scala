package services.impl

import common.HEException
import models._
import models.db.TechVoteLog
import models.domain.Identifiable.Id
import models.domain.TechRating
import reactivemongo.bson.BSONObjectID
import repositories.{TechRepository, TechVoteLogRepository}
import services.TechService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TechServiceImpl(techRepository: TechRepository,
                      techVoteLogRepository: TechVoteLogRepository) extends TechService {

  override def insert(name: String, userId: domain.Identifiable.Id) =
    techRepository.insert(
      name = name.toLowerCase,
      authorId = BSONObjectID(userId)
    ).map(_.stringify)

  override def all() =
    techRepository.all().map(_.map { tech =>
      domain.Tech(
        id = tech._id.stringify,
        authorId = tech.authorId.stringify,
        name = tech.name,
        rating = TechRating(tech.upVotes,tech.downVotes))
    }.sortBy( _.rating.map(-1 * _.value).getOrElse(0.0)))

  override def voteUp(id: Id, userId: Id) = vote(id, userId, 1, techVoteLogRepository.logUpVote, canVoteUp)
  override def voteDown(id: Id, userId: Id) = vote(id, userId, -1, techVoteLogRepository.logDownVote, canVoteDown)

  override def canVoteUp(id: Id, userId: Id) = canVote(id, userId, TechVoteLog.downVoteValue)
  override def canVoteDown(id: Id, userId: Id) = canVote(id, userId, TechVoteLog.upVoteValue)

  private def canVote(id: Id, userId: Id, value: Int) = techVoteLogRepository.latestVote(BSONObjectID(id), BSONObjectID(userId))
      .map(_.map(_.value == value)).map(_.getOrElse(true))

  private def vote(id: Id,
                   userId: Id,
                   delta: Int,
                   logVote: (db.Identifiable.Id, db.Identifiable.Id) => Future[Unit],
                   canVote: (Id, Id) => Future[Boolean]): Future[Unit] = {
    canVote(id, userId).map { can =>
      if (can) {
        val dbId = BSONObjectID(id)
        val dbUserId = BSONObjectID(userId)
        techRepository.updateRating(dbId, delta).map { Unit =>
          logVote(dbId, dbUserId)
        }
      }
      else
        throw new HEException(s"You cannot vote! [$id, $userId, $delta]")
    }
  }
}
