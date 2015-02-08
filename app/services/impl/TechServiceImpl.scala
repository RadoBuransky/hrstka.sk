package services.impl

import models.db.TechVoteLog
import models.domain
import models.domain.Identifiable.Id
import models.domain.TechRating
import reactivemongo.bson.BSONObjectID
import repositories.{TechRepository, TechVoteLogRepository}
import services.TechService

import scala.concurrent.ExecutionContext.Implicits.global

class TechServiceImpl(techRepository: TechRepository,
                      techVoteLogRepository: TechVoteLogRepository) extends TechService {

  override def insert(name: String, userId: domain.Identifiable.Id) =
    techRepository.insert(
      name = name,
      authorId = BSONObjectID(userId)
    ).map(_.stringify)

  override def all() =
    techRepository.all().map(_.map { tech =>
      domain.Tech(
        id = tech._id.stringify,
        author = tech.authorId.stringify,
        name = tech.name,
        rating = TechRating(tech.upVotes,tech.downVotes))
    })

  override def voteUp(id: Id, userId: Id) =  vote(id, userId, 1).map { Unit =>
    techVoteLogRepository.logUpVote(BSONObjectID(id), BSONObjectID(userId))
    }

  override def voteDown(id: Id, userId: Id) =  vote(id, userId, -1).map { Unit =>
    techVoteLogRepository.logDownVote(BSONObjectID(id), BSONObjectID(userId))
  }

  override def canVoteUp(id: Id, userId: Id) = canVote(id, userId, TechVoteLog.downVoteValue).map(_.getOrElse(true))
  override def canVoteDown(id: Id, userId: Id) = canVote(id, userId, TechVoteLog.upVoteValue).map(_.getOrElse(true))

  private def canVote(id: Id, userId: Id, value: Int) = techVoteLogRepository.latestVote(BSONObjectID(id), BSONObjectID(userId))
      .map(_.map(_.value == value))

  private def vote(id: Id, userId: Id, delta: Int) = techRepository.updateRating(BSONObjectID(id), delta)
}
