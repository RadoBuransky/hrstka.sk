package services.impl

import java.net.URL

import models.domain
import models.domain.Comp
import models.domain.Identifiable.{Id, _}
import repositories.{CompRepository, CompTechRepository, TechRepository}
import services.{TechService, CompService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CompServiceImpl(compRepository: CompRepository,
                      compTechRepository: CompTechRepository,
                      techService: TechService) extends CompService {
  override def insert(name: String, website: URL, location: String, codersCount: Option[Int], femaleCodersCount: Option[Int],
                      note: String, userId: Id): Future[Id] =
    compRepository.insert(
      name              = name,
      website           = website.toString,
      location          = location,
      codersCount       = codersCount,
      femaleCodersCount = femaleCodersCount,
      note              = note,
      authorId          = userId
    ).map(_.stringify)

  override def all(): Future[Seq[Comp]] = compRepository.all().map(_.map(domain.Comp(_)))

  override def addTech(techName: String, compId: Id, userId: Id): Future[Id] = {
    techService.getOrInsert(techName, userId).flatMap { techId =>
      compTechRepository.add(
        authorId  = userId,
        compId    = compId,
        techId    = techId
      ).map(_.stringify)
    }
  }

  override def removeTech(compId: Id, techId: Id, userId: Id): Future[Unit] =
    compTechRepository.remove(compId, techId, userId)

  override def get(compId: Id): Future[Comp] = compRepository.get(compId).map(Comp(_))
  override def update(comp: Comp): Future[Unit] = compRepository.update(
    compId            = comp.id,
    name              = comp.name,
    website           = comp.website.toString,
    location          = comp.location,
    codersCount       = comp.codersCount,
    femaleCodersCount = comp.femaleCodersCount,
    note              = comp.note)
}
