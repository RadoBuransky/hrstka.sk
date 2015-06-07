package sk.hrstka.services.impl

import com.google.inject.{Inject, Singleton}
import sk.hrstka
import sk.hrstka.common.Logging
import sk.hrstka.models.db
import sk.hrstka.models.domain._
import sk.hrstka.repositories._
import sk.hrstka.services.TechService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
final class TechServiceImpl @Inject() (techRepository: TechRepository,
                                       techVoteRepository: TechVoteRepository) extends TechService with Logging {
  import sk.hrstka.models.domain.Identifiable._

  override def upsert(tech: hrstka.models.domain.Tech): Future[Id] =
    techRepository.upsert(db.Tech(
      _id             = tech.id,
      handle          = tech.handle,
      categoryHandle  = tech.category.handle,
      name            = tech.name,
      website         = tech.website.toString
    )).map(Identifiable.fromBSON)

  def getByHandle(handle: hrstka.models.domain.Handle) = techRepository.getByHandle(handle).map(TechFactory(_))

  override def allRatings(): Future[Seq[TechRating]] =
    // Get all technology votes for all users
    techVoteRepository.all(None).flatMap { dbTechVotes =>
      // Map to domain model
      val allTechVotes = dbTechVotes.map(TechVoteFactory.apply)
      // Get all technologies
      techRepository.all().map { techs =>
        // Map to domain model
        val unordered = techs.map(dbTech => techRating(TechFactory(dbTech), allTechVotes))

        // Covert to a ordered sequence and sort
        unordered.toSeq.sortBy(-1 * _.value)
      }
    }

  override def voteUp(techId: Id, userId: Id) = voteDelta(techId, userId, 1)
  override def voteDown(techId: Id, userId: Id) = voteDelta(techId, userId, -1)
  override def votesFor(userId: Id): Future[Traversable[TechVote]] =
    techVoteRepository.all(Some(userId)).map(_.map(TechVoteFactory.apply))

  override def allCategories(): Future[Traversable[TechCategory]] = Future.successful(TechCategory.allCategories)

  /**
   * Computes technology rating. Ignores votes with zero value. 100% if all votes have highest value.
   *
   * @param tech Technology for which to compute rating value.
   * @param allTechVotes Votes for all users and technologies.
   * @return Rating value, a number between 0.0 and 100.0
   */
  private def techRating(tech: Tech, allTechVotes: Traversable[TechVote]): TechRating = {
    val techVotes = allTechVotes.filter(tv => tv.techId == tech.id && tv.value != 0).map(_.value)
    TechRatingFactory(tech, techVotes.filter(_ > 0).sum, techVotes.size)
  }

  private def voteDelta(techId: Id, userId: Id, delta: Int): Future[Unit] = {
    techVoteRepository.findValue(techId, userId).map { latestVoteOption =>
      val newVoteValue = latestVoteOption.getOrElse(0) + delta
      if ((newVoteValue <= TechRatingFactory.maxVoteValue) &&
        (newVoteValue >= TechRatingFactory.minVoteValue))
        techVoteRepository.vote(techId, userId, newVoteValue)
    }
  }

}