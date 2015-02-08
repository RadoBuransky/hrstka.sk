package repositories

import models.db.Identifiable._
import models.db.TechVoteLog

import scala.concurrent.Future

trait TechVoteLogRepository {
  def logUpVote(techId: Id, authorId: Id): Future[Unit]
  def logDownVote(techId: Id, authorId: Id): Future[Unit]
  def latestVote(techId: Id, authorId: Id): Future[Option[TechVoteLog]]
}
