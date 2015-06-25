package sk.hrstka.services.impl

import com.google.inject.{Inject, Singleton}
import sk.hrstka
import sk.hrstka.models.domain.{Handle, _}
import sk.hrstka.repositories.{CompRepository, CompVoteRepository}
import sk.hrstka.services.{CompService, LocationService, TechService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
final class CompServiceImpl @Inject() (compRepository: CompRepository,
                                       compVoteRepository: CompVoteRepository,
                                       techService: TechService,
                                       locationService: LocationService) extends CompService {
  import Identifiable._

  override def upsert(comp: Comp, techHandles: Set[hrstka.models.domain.Handle], userId: Id): Future[Id] = {
    compRepository.upsert(hrstka.models.db.Comp(
      _id = comp.id,
      authorId = userId,
      name = comp.name,
      website = comp.website.toString,
      city = comp.city.handle,
      employeeCount = comp.employeeCount,
      codersCount = comp.codersCount,
      femaleCodersCount = comp.femaleCodersCount,
      note = comp.note,
      products = comp.products,
      services = comp.services,
      internal = comp.internal,
      techs = techHandles.map(_.value),
      joel = comp.joel
    )).map(Identifiable.fromBSON)
  }

  override def all(city: Option[hrstka.models.domain.Handle], tech: Option[hrstka.models.domain.Handle]): Future[Seq[Comp]] = {
    // Get all technologies with ratings
    techService.allRatings().flatMap { techRatings =>
      // Get all companies for the city and the technology
      compRepository.all(city.map(_.value), tech.map(_.value)).flatMap { dbComps =>
        // Convert DB entities to domain
        val comps = Future.sequence(dbComps.map(dbCompToDomain(techRatings, _)))

        // Sort by company rating
        comps.map(_.toSeq.sortBy(-1 * CompRatingFactory(_).value))
      }
    }
  }

  override def get(compId: Id): Future[Comp] =
    techService.allRatings().flatMap { techRatings =>
      compRepository.get(compId).flatMap(dbCompToDomain(techRatings, _))
    }

  override def topWomen(): Future[Seq[Comp]] = {
    def womenRank(comp: Comp): Option[Double] = comp.codersCount.flatMap {
      case 0 => None
      case codersCount => comp.femaleCodersCount.flatMap {
        case 0 => None
        case femaleCodersCount => Some(femaleCodersCount.toDouble / codersCount.toDouble)
      }
    }

    all(None, None).map { comps =>
      // Compute rank, filter only those with known emplyee/women count, sort and take top few
      comps.map(comp => (comp, womenRank(comp))).filter(_._2.isDefined).sortBy(-1 * _._2.get).map(_._1).take(42)
    }
  }

  override def voteUp(compId: Id, userId: Id): Future[Unit] = voteDelta(compId, userId, 1)
  override def voteDown(compId: Id, userId: Id): Future[Unit] = voteDelta(compId, userId, -1)

  private def dbCompToDomain(techRatings: Seq[TechRating], comp: hrstka.models.db.Comp): Future[Comp] = {
    locationService.get(Handle(comp.city)).map { city =>
      CompFactory(comp, techRatings.filter(t => comp.techs.contains(t.tech.handle.value)), city)
    }
  }

  private def voteDelta(compId: Id, userId: Id, delta: Int): Future[Unit] = {
    compVoteRepository.findValue(compId, userId).map { latestVoteOption =>
      val newVoteValue = latestVoteOption.getOrElse(0) + delta
      if ((newVoteValue <= CompRatingFactory.maxVoteValue) &&
        (newVoteValue >= CompRatingFactory.minVoteValue))
        compVoteRepository.vote(compId, userId, newVoteValue)
    }
  }
}
