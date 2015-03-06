package repositories

import models.db.Identifiable.Id

import scala.concurrent.Future

trait VoteRepository {
  def vote(id: Id, authorId: Id, value: Int): Future[Boolean]
  def getValue(id: Id, authorId: Id): Future[Option[Int]]
}
