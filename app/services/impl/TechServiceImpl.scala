package services.impl

import com.google.inject.{Inject, Singleton}
import models._
import models.db.Identifiable
import models.domain.Identifiable.{Id, _}
import models.domain.{Handle, Tech, TechRating, TechVote}
import repositories._
import services.TechService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
final class TechServiceImpl @Inject() (techRepository: TechRepository,
                                       techVoteRepository: TechVoteRepository,
                                       techVoteLogRepository: TechVoteLogRepository) extends TechService {
  def get(handle: Handle) = techRepository.getByHandle(handle).map(domain.Tech(_))

  override def upsert(tech: Tech): Future[Id] =
    techRepository.upsert(db.Tech(
      _id             = Identifiable.empty,
      handle          = tech.handle,
      categoryHandle  = tech.category.handle,
      name            = tech.name,
      website         = tech.website.toString
    )).map(_.stringify)

  override def allRatings(): Future[Seq[TechRating]] =
    // TODO: Compute rating ...
    techRepository.all().map(_.map(dbTech => TechRating(Tech(dbTech), 0.0)))

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
    techVoteRepository.vote(id, userId, value).map { changed =>
      if (changed)
        techVoteLogRepository.logVote(id, userId, value)
    }
  }

  override def votesFor(userId: Id): Future[Seq[TechVote]] =
    techVoteRepository.getAll(userId).map(_.map(TechVote(_)))

}