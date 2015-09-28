package sk.hrstka.services.impl

import com.google.inject.{ImplementedBy, Inject, Singleton}
import sk.hrstka
import sk.hrstka.models.db
import sk.hrstka.models.domain.{Handle, _}
import sk.hrstka.repositories.{CompRepository, CompVoteRepository}
import sk.hrstka.services.{CompService, LocationService, TechService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Marker trait.
 */
@ImplementedBy(classOf[CompServiceImpl])
private[impl] trait NotCachedCompService extends CompService

@Singleton
final class CompServiceImpl @Inject() (compRepository: CompRepository,
                                       compVoteRepository: CompVoteRepository,
                                       techService: TechService,
                                       locationService: LocationService) extends NotCachedCompService {
  import Identifiable._

  override def upsert(comp: Comp, techHandles: Set[hrstka.models.domain.Handle], userId: Id): Future[BusinessNumber] = {
    compRepository.upsert(hrstka.models.db.Comp(
      _id = comp.id,
      authorId = userId,
      name = comp.name,
      website = comp.website.toString,
      cities = comp.cities.map(_.handle.value),
      businessNumber = comp.businessNumber.value,
      employeeCount = comp.employeeCount,
      codersCount = comp.codersCount,
      femaleCodersCount = comp.femaleCodersCount,
      note = comp.markdownNote,
      products = comp.products,
      services = comp.services,
      internal = comp.internal,
      techs = techHandles.map(_.value),
      joel = comp.joel,
      govBiz = comp.govBiz
    )).map(_ => comp.businessNumber)
  }

  override def all(city: Option[hrstka.models.domain.Handle], tech: Option[hrstka.models.domain.Handle]): Future[Seq[CompRating]] =
    // Get all technologies with ratings
    techService.allRatings().flatMap { techRatings =>
      // Get all companies for the city and the technology
      compRepository.all(city.map(_.value), tech.map(_.value)).flatMap { dbComps =>
        // Convert DB entities to domain
        Future.sequence(dbComps.map(dbCompToDomain(techRatings, _))).flatMap { comps =>
          // Get all votes for all companies
          compVoteRepository.all(None).map { allCompVotes =>
            // Convert to company ratings
            val compRatings = comps.map(compRating(_, allCompVotes))

            // Sort by rating value
            compRatings.toSeq.sortBy(-1 * _.value)
          }
        }
      }
    }

  override def search(compSearch: CompSearch): Future[Seq[CompRating]] = ???

  override def get(businessNumber: BusinessNumber): Future[Comp] =
    techService.allRatings().flatMap { techRatings =>
      compRepository.get(businessNumber.value).flatMap(dbCompToDomain(techRatings, _))
    }

  override def topWomen(): Future[Seq[CompRating]] = {
    def womenRank(comp: Comp): Option[Double] = comp.codersCount.flatMap {
      case 0 => None
      case codersCount => comp.femaleCodersCount.flatMap {
        case 0 => None
        case femaleCodersCount => Some(femaleCodersCount.toDouble / codersCount.toDouble)
      }
    }

    all(None, None).map { compRatings =>
      // Compute rank, filter only those with known emplyee/women count, sort and take top few
      compRatings.map(compRating => (compRating, womenRank(compRating.comp))).filter(_._2.isDefined).sortBy(-1 * _._2.get).map(_._1).take(42)
    }
  }

  override def voteFor(businessNumber: BusinessNumber, userId: Id): Future[Option[CompVote]] =
    compRepository.get(businessNumber.value).flatMap { dbComp =>
      compVoteRepository.findValue(dbComp._id, userId).map { voteOption =>
        voteOption.map(CompVote(dbComp._id, userId, _))
      }
    }

  override def voteUp(businessNumber: BusinessNumber, userId: Id): Future[Unit] = voteDelta(businessNumber, userId, 1)
  override def voteDown(businessNumber: BusinessNumber, userId: Id): Future[Unit] = voteDelta(businessNumber, userId, -1)

  private def compRating(comp: Comp, allCompVotes: Traversable[db.CompVote]): CompRating = {
    val upVotesValue = allCompVotes.withFilter(v => Identifiable.fromBSON(v.entityId) == comp.id && v.value > 0).map(_.value).sum
    val voteCount = allCompVotes.count(v => Identifiable.fromBSON(v.entityId) == comp.id && v.value != 0)
    CompRatingFactory(comp, upVotesValue, voteCount)
  }

  private def dbCompToDomain(techRatings: Seq[TechRating], comp: hrstka.models.db.Comp): Future[Comp] = {
    Future.sequence(comp.cities.map(Handle.apply).map(locationService.city)).map { cities =>
      CompFactory(comp, techRatings.filter(t => comp.techs.contains(t.tech.handle.value)), cities)
    }
  }

  private def voteDelta(businessNumber: BusinessNumber, userId: Id, delta: Int): Future[Unit] = {
    compRepository.get(businessNumber.value).flatMap { dbComp =>
      compVoteRepository.findValue(dbComp._id, userId).map { latestVoteOption =>
        val newVoteValue = latestVoteOption.getOrElse(0) + delta
        if ((newVoteValue <= CompRatingFactory.maxVoteValue) &&
          (newVoteValue >= CompRatingFactory.minVoteValue))
          compVoteRepository.vote(dbComp._id, userId, newVoteValue)
      }
    }
  }
}
