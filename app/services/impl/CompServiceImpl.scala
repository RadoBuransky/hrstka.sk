package services.impl

import java.net.URL

import models.domain
import models.domain.Comp
import models.domain.Identifiable.Id
import reactivemongo.bson.BSONObjectID
import repositories.CompRepository
import services.CompService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CompServiceImpl(compRepository: CompRepository) extends CompService {
  override def insert(name: String, website: URL, userId: Id): Future[Id] =
    compRepository.insert(
      name = name.toLowerCase,
      website = website.toString,
      authorId = BSONObjectID(userId)
    ).map(_.stringify)

  override def all(): Future[Seq[Comp]] = compRepository.all().map(_.map { comp =>
    domain.Comp(
      id = comp._id.stringify,
      authorId = comp.authorId.stringify,
      name = comp.name,
      website = new URL(comp.website))
  })
}
