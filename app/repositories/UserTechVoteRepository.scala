package repositories

import models.db.Identifiable.Id

import scala.concurrent.Future

trait UserTechVoteRepository {
  def updateTechVote(userId: Id, techId: Id, value: Int): Future[Boolean]
}
