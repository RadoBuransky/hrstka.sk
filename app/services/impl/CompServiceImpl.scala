package services.impl

import java.net.URL

import models.domain.{CompQuery, Comp}
import models.domain.Identifiable.{Id, _}
import models.{db, domain}
import repositories.CompRepository
import services.{CompService, TechService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CompServiceImpl(compRepository: CompRepository,
                      techService: TechService) extends CompService {
  override def insert(name: String,
                      website: URL,
                      citySk: String,
                      employeeCount: Option[Int],
                      codersCount: Option[Int],
                      femaleCodersCount: Option[Int],
                      note: String,
                      userId: Id,
                      products: Boolean,
                      services: Boolean,
                      internal: Boolean,
                      techNames: Seq[String],
                      joel: Set[Int]): Future[Id] = {
    techNamesToIds(techNames).flatMap { techIds =>

      // TODO: Get city ID for the slovak name
      val cityId = db.Identifiable.empty

      compRepository.upsert(db.Comp(
        _id               = db.Identifiable.empty,
        authorId          = userId,
        name              = name,
        website           = website.toString,
        city              = cityId,
        employeeCount     = employeeCount,
        codersCount       = codersCount,
        femaleCodersCount = femaleCodersCount,
        note              = note,
        products          = products,
        services          = services,
        internal          = internal,
        techs             = techIds,
        joel              = joel

      )).map(_.stringify)
    }
  }

  override def all(): Future[Seq[Comp]] = {
    compRepository.all().flatMap { comps =>
      Future.sequence(comps.map(dbCompToDomain))
    }
  }

  override def get(compId: Id): Future[Comp] = compRepository.get(compId).flatMap(dbCompToDomain)
  override def update(comp: Comp, techNames: Seq[String], userId: Id): Future[Unit] = techNamesToIds(techNames).map { techIds =>
    // TODO: What if user modifies the city?
    compRepository.upsert(db.Comp(
      _id               = comp.id,
      authorId          = userId,
      name              = comp.name,
      website           = comp.website.toString,
      city              = comp.city.id,
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
    techService.all().map { techs =>
      val ids = comp.techs.map(_.stringify)

      // TODO: Get db.City
      val city = db.City(comp.city, "")
      domain.Comp(comp, techs.filter(t => ids.contains(t.id)), city)
    }
  }

  private def techNamesToIds(techNames: Seq[String]): Future[Seq[db.Identifiable.Id]] = {
    techService.all().map { allTechs =>
      allTechs.filter(t => techNames.contains(t.name)).map(t => db.Identifiable(t.id))
    }
  }

  override def find(query: CompQuery, location: Option[String], tech: Option[String]): Future[Seq[Comp]] = ???
}
