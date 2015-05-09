package services.impl

import models.domain.Identifiable.{Id, _}
import models.domain.{Comp, CompQuery, Handle}
import models.{db, domain}
import repositories.CompRepository
import services.{CompService, LocationService, TechService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CompServiceImpl(compRepository: CompRepository,
                      techService: TechService,
                      locationService: LocationService) extends CompService {
  override def all(): Future[Seq[Comp]] = {
    compRepository.all().flatMap { comps =>
      Future.sequence(comps.map(dbCompToDomain))
    }
  }

  override def get(compId: Id): Future[Comp] = compRepository.get(compId).flatMap(dbCompToDomain)
  override def upsert(comp: Comp, techNames: Seq[String], userId: Id): Future[Unit] =
    techNamesToIds(techNames).map { techIds =>
      compRepository.upsert(db.Comp(
        _id               = comp.id,
        authorId          = userId,
        name              = comp.name,
        website           = comp.website.toString,
        city              = comp.city.handle,
        employeeCount     = comp.employeeCount,
        codersCount       = comp.codersCount,
        femaleCodersCount = comp.femaleCodersCount,
        note              = comp.note,
        products          = comp.products,
        services          = comp.services,
        internal          = comp.internal,
        techs             = techIds,
        joel              = comp.joel
    ))
  }

  private def dbCompToDomain(comp: db.Comp): Future[domain.Comp] = {
    techService.all().flatMap { techs =>
      val ids = comp.techs.map(_.stringify)
      locationService.get(Handle(comp.city)).map { city =>
        domain.Comp(comp, techs.filter(t => ids.contains(t.id)), city)
      }
    }
  }

  private def techNamesToIds(techNames: Seq[String]): Future[Seq[db.Identifiable.Id]] = {
    techService.all().map { allTechs =>
      allTechs.filter(t => techNames.contains(t.name)).map(t => db.Identifiable(t.id))
    }
  }

  override def find(query: CompQuery, location: Option[String], tech: Option[String]): Future[Seq[Comp]] = ???
}
