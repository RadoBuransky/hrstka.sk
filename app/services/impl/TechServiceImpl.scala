package services.impl

import com.google.inject.{Inject, Singleton}
import models._
import models.db.Identifiable
import models.domain.Identifiable.{Id, _}
import models.domain._
import repositories._
import services.TechService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
final class TechServiceImpl @Inject() (techRepository: TechRepository,
                                       techVoteRepository: TechVoteRepository) extends TechService {
  def get(handle: Handle) = techRepository.getByHandle(handle).map(domain.TechFactory(_))

  override def upsert(tech: Tech): Future[Id] =
    techRepository.upsert(db.Tech(
      _id             = Identifiable.empty,
      handle          = tech.handle,
      categoryHandle  = tech.category.handle,
      name            = tech.name,
      website         = tech.website.toString
    )).map(_.stringify)

  override def allRatings(): Future[Seq[TechRating]] =
    techVoteRepository.all(None).flatMap { dbTechVotes =>
      val allTechVotes = dbTechVotes.map(TechVoteFactory.apply)
      techRepository.all().map { techs =>
        techs.map { dbTech =>
          val tech = TechFactory(dbTech)
          TechRating(tech, techRatingValue(tech, allTechVotes))
        }
      }
    }

  override def voteUp(id: Id, userId: Id) = voteDelta(id, userId, 1)
  override def voteDown(id: Id, userId: Id) = voteDelta(id, userId, -1)
  override def votesFor(userId: Id): Future[Seq[TechVote]] =
    techVoteRepository.all(Some(userId)).map(_.map(TechVoteFactory.apply))

  override def allCategories(): Future[Seq[TechCategory]] = Future.successful(TechCategory.allCategories)

  /**
   * Computes technology rating. Ignores votes with zero value. 100% if all votes have highest value.
   *
   * @param tech Technology for which to compute rating value.
   * @param allTechVotes Votes for all users and technologies.
   * @return Rating value, a number between 0.0 and 100.0
   */
  private def techRatingValue(tech: Tech, allTechVotes: Iterable[TechVote]): Double = {
    val techVotes = allTechVotes.filter(tv => tv.techId == tech.id && tv.value != 0).map(_.value)
    if (techVotes.isEmpty)
      0.0
    else
      techVotes.filter(_ > 0).sum.toDouble / (techVotes.size * TechRatingFactory.maxVoteValue).toDouble
  }

  private def voteDelta(id: Id, userId: Id, delta: Int): Future[Unit] = {
    techVoteRepository.findValue(id, userId).map { latestVoteOption =>
      val newVoteValue = latestVoteOption.getOrElse(0) + delta
      if ((newVoteValue <= TechRatingFactory.maxVoteValue) &&
        (newVoteValue >= TechRatingFactory.minVoteValue))
        techVoteRepository.vote(id, userId, newVoteValue)
    }
  }

}