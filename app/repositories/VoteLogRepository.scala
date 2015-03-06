package repositories

import models.db.Identifiable._

import scala.concurrent.Future

trait VoteLogRepository {
  def logVote(id: Id, authorId: Id, value: Int): Future[Unit]
}
