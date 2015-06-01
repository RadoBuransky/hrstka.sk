package services.impl

import com.google.inject.{Inject, Singleton}
import common.HEException
import models._
import models.db.Identifiable
import models.domain.Identifiable.{Id, _}
import models.domain.{Handle, Tech, TechRating, TechVote}
import reactivemongo.bson.BSONDocument
import repositories._
import services.TechService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
final class TechServiceImpl @Inject() (techRepository: TechRepository,
                                       techVoteRepository: TechVoteRepository,
                                       techVoteLogRepository: TechVoteLogRepository) extends TechService {
  def get(handle: Handle) = all().map(_.find(_.handle == handle) match {
    case Some(tech) => tech
    case None => throw new HEException(s"Tech doesn't exist! [${handle.value}]")
  })

  override def insert(name: String, userId: domain.Identifiable.Id) =
    techRepository.upsert(db.Tech(
      _id           = Identifiable.empty,
      authorId      = userId,
      handle        = name.toLowerCase,
      upVotes       = 0,
      upVotesValue  = 0,
      downVotes     = 0
    )).map(_.stringify)

  override def getOrInsert(name: String, userId: Id): Future[Id] = {
    techRepository.all().map(_.find(_.handle == name)).flatMap {
      case Some(tech) => Future(tech._id)
      case None => insert(name, userId)
    }
  }

  override def all() =
    techRepository.all().map(_.map(Tech(_)).sortBy(-1 * _.rating.value))

  override def topTechs(): Future[Seq[Tech]] = all().map(_.take(10))

  override def voteUp(id: Id, userId: Id) = voteDelta(id, userId, 1)
  override def voteDown(id: Id, userId: Id) = voteDelta(id, userId, -1)
  
  private def voteDelta(id: Id, userId: Id, delta: Int): Future[Unit] = {
    techVoteRepository.getValue(id, userId).map { latestVoteOption =>
      val newVoteValue = latestVoteOption.getOrElse(0) + delta
      if ((newVoteValue <= TechRating.maxVoteValue) &&
        (newVoteValue >= TechRating.minVoteValue))
        vote(id, userId, delta, newVoteValue)
    }
  }

  private def vote(id: Id, userId: Id, delta: Int, value: Int): Future[Unit] = {
    techRepository.updateRating(id, delta, value).map { Unit =>
      techVoteRepository.vote(id, userId, value).map { changed =>
        if (changed)
          techVoteLogRepository.logVote(id, userId, value)
      }
    }
  }

  override def votesFor(userId: Id): Future[Seq[TechVote]] =
    techVoteRepository.getAll(userId).map(_.map(TechVote(_)))

}