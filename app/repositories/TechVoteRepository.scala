package repositories

import models.db.Identifiable._
import models.db.TechVote

import scala.concurrent.Future

trait TechVoteRepository {
  def upVote(techId: Id, authorId: Id): Future[Unit]
  def downVote(techId: Id, authorId: Id): Future[Unit]
  def latestVote(techId: Id, authorId: Id): Future[Option[TechVote]]
}
