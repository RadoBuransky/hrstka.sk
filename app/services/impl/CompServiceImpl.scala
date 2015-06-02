package services.impl

import com.google.inject.{Inject, Singleton}
import models.db.Identifiable
import models.domain.Identifiable.{Id, _}
import models.domain._
import models.{db, domain}
import repositories.CompRepository
import services.{CompService, LocationService, TechService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
final class CompServiceImpl @Inject() (compRepository: CompRepository,
                                       techService: TechService,
                                       locationService: LocationService) extends CompService {
  override def all(city: Option[Handle], tech: Option[Handle]): Future[Seq[Comp]] = {
    compRepository.all(city.map(_.value), tech.map(_.value)).flatMap { comps =>
      Future.sequence(comps.map(dbCompToDomain))
    }
  }

  override def get(compId: Id): Future[Comp] = compRepository.get(compId).flatMap(dbCompToDomain)
  override def upsert(comp: Comp, techHandles: Seq[Handle], userId: Id): Future[Id] = {
    compRepository.upsert(db.Comp(
      _id = if (comp.id.isEmpty) Identifiable.empty else comp.id,
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
    )).map(_.stringify)
  }

  private def dbCompToDomain(comp: db.Comp): Future[domain.Comp] = {
    techService.allRatings().flatMap { techRatings =>
      locationService.get(Handle(comp.city)).map { city =>
        domain.Comp(comp, techRatings.filter(t => comp.techs.contains(t.tech.handle.value)), city)
      }
    }
  }

  override def find(query: CompQuery): Future[Seq[Comp]] = ???

  override def topCities(): Future[Seq[City]] = {
    locationService.all().flatMap { cities =>
      all().map { comps =>
        cities.sortBy { city =>
          -1 * comps.count(_.city.handle == city.handle)
        }
      }
    }
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
}
