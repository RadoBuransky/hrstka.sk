package sk.hrstka.repositories.scripts

import com.google.inject.ImplementedBy
import sk.hrstka.repositories.scripts.mongoDb.MongoDbManager

import scala.concurrent.Future

/**
 * Database manager.
 */
@ImplementedBy(classOf[MongoDbManager])
trait DbManager {
  /**
   * Executes whatever is needed at the application startup.
   */
  def applicationInit(): Future[_]
}