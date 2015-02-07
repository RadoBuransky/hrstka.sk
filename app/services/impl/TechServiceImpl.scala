package services.impl

import models.domain
import models.domain.Identifiable.Id
import models.domain.TechRating
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
        rating = TechRating(tech.upVotes,tech.downVotes))
    })

  override def voteUp(id: Id, userId: Id) = vote(id, userId, 1)
  override def voteDown(id: Id, userId: Id) =  vote(id, userId, -1)
  private def vote(id: Id, userId: Id, delta: Int) = techRepository.updateRating(BSONObjectID(id), delta)
}
