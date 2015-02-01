package services.impl

import models.domain
import models.domain.{TechRating, User}
import reactivemongo.bson.BSONObjectID
import repositories.TechRepository
import services.TechService

import scala.concurrent.ExecutionContext.Implicits.global

class TechServiceImpl(techRepository: TechRepository) extends TechService {
  override def insert(name: String, userId: domain.Identifiable.Id) =
    techRepository.insert(
      name = name,
      author = BSONObjectID(userId)
    )

  override def all() =
    techRepository.all().map(_.map { tech =>
      domain.Tech(
        id = tech._id.get.stringify,
        author = tech.author.stringify,
        name = tech.name,
        rating = tech.rating.map(TechRating))
    })
}
