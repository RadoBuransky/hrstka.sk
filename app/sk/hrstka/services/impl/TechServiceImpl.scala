package sk.hrstka.services.impl

import com.google.inject.{ImplementedBy, Inject, Singleton}
import sk.hrstka
import sk.hrstka.common.{HrstkaException, Logging}
import sk.hrstka.models.db
import sk.hrstka.models.domain._
import sk.hrstka.repositories._
import sk.hrstka.services.TechService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Marker trait.
 */
@ImplementedBy(classOf[TechServiceImpl])
private[impl] trait NotCachedTechService extends TechService

@Singleton
final class TechServiceImpl @Inject() (techRepository: TechRepository,
                                       techVoteRepository: TechVoteRepository,
                                       compRepository: CompRepository)
  extends NotCachedTechService with Logging {
  import sk.hrstka.models.domain.Identifiable._

  override def upsert(tech: hrstka.models.domain.Tech): Future[Handle] =
    techRepository.upsert(db.Tech(
      _id             = tech.id,
      handle          = tech.handle,
      categoryHandle  = tech.category.handle,
      name            = tech.name,
      website         = tech.website.toString
    )).map(_ => tech.handle)

  override def remove(handle: Handle): Future[Handle] = {
    // Get all companies for the technology
    compRepository.all().flatMap { comps =>
      if (comps.exists(_.techs.contains(handle.value)))
        throw new HrstkaException(s"Cannot remove technology while it's used! [$comps]")
      techRepository.remove(handle.value).map(Handle)
    }
  }

  def getByHandle(handle: Handle) = techRepository.getByHandle(handle).map(TechFactory(_))

  override def allRatings(): Future[Seq[TechRating]] =
    // Get all technology votes for all users
    techVoteRepository.all(None).flatMap { dbTechVotes =>
      // Map to domain model
      val allTechVotes = dbTechVotes.map(VoteFactory.apply)
      // Get all technologies
      techRepository.all().map { techs =>
        // Map to domain model
        val unordered = techs.map(dbTech => techRating(TechFactory(dbTech), allTechVotes))

        // Covert to a ordered sequence and sort
        unordered.toSeq.sortBy(-1 * _.value)
      }
    }

  override def voteUp(handle: Handle, userId: Id) = voteDelta(handle, userId, 1)
  override def voteDown(handle: Handle, userId: Id) = voteDelta(handle, userId, -1)
  override def votesFor(userId: Id): Future[Traversable[TechVote]] =
    techVoteRepository.all(Some(userId)).map(_.map(VoteFactory.apply))

  override def allCategories(): Future[Traversable[TechCategory]] = Future.successful(TechCategory.allCategories)

  /**
   * Computes technology rating. Ignores votes with zero value. 100% if all votes have highest value.
   *
   * @param tech Technology for which to compute rating value.
   * @param allTechVotes Votes for all users and technologies.
   * @return Rating value, a number between 0.0 and 100.0
   */
  private def techRating(tech: Tech, allTechVotes: Traversable[TechVote]): TechRating = {
    val techVotes = allTechVotes.filter(tv => tv.entityId == tech.id && tv.value != 0).map(_.value)
    TechRatingFactory(tech, techVotes.filter(_ > 0).sum, techVotes.size)
  }

  private def voteDelta(handle: Handle, userId: Id, delta: Int): Future[Unit] = {
    techRepository.getByHandle(handle).flatMap { dbTech =>
      techVoteRepository.findValue(dbTech._id, userId).map { latestVoteOption =>
        val newVoteValue = latestVoteOption.getOrElse(0) + delta
        if ((newVoteValue <= TechRatingFactory.maxVoteValue) &&
          (newVoteValue >= TechRatingFactory.minVoteValue))
          techVoteRepository.vote(dbTech._id, userId, newVoteValue)
      }
    }
  }
}