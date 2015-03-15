package services.impl

import java.net.URL

import models.domain
import models.domain.Comp
import models.domain.Identifiable.{Id, _}
import repositories.{CompRepository, CompTechRepository, TechRepository}
import services.CompService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CompServiceImpl(compRepository: CompRepository,
                      compTechRepository: CompTechRepository,
                      techRepository: TechRepository) extends CompService {
  override def insert(name: String, website: URL, location: String, codersCount: Option[Int], femaleCodersCount: Option[Int],
                      note: String): Future[Id] =
    compRepository.insert(
      name              = name,
      website           = website.toString,
      location          = location,
      codersCount       = codersCount,
      femaleCodersCount = femaleCodersCount,
      note              = note
    ).map(_.stringify)

  override def all(): Future[Seq[Comp]] = {
    compRepository.all().flatMap { comps =>
      Future.sequence(comps.map { comp =>
        compTechRepository.getTechs(comp._id).flatMap { techIds =>
          Future.sequence(techIds.map(techRepository.get)).map { techs =>
            domain.Comp(
              id                = comp._id.stringify,
              name              = comp.name,
              website           = new URL(comp.website),
              location          = comp.location,
              codersCount       = comp.codersCount,
              femaleCodersCount = comp.femaleCodersCount,
              note              = comp.note)
          }
        }
      })
    }
  }

  override def addTech(compId: Id, techId: Id, userId: Id): Future[Id] = {
    compTechRepository.add(
      authorId  = userId,
      compId    = compId,
      techId    = techId
    ).map(_.stringify)
  }

  override def removeTech(compId: Id, techId: Id, userId: Id): Future[Unit] =
    compTechRepository.remove(compId, techId, userId)
}
