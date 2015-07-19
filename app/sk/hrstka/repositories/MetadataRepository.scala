package sk.hrstka.repositories

import com.google.inject.ImplementedBy
import sk.hrstka.models.db.Identifiable.Id
import sk.hrstka.models.db.Metadata
import sk.hrstka.repositories.mongoDb.MongoMetadataRepository

import scala.concurrent.Future

/**
 * Repository for metadata.
 */
@ImplementedBy(classOf[MongoMetadataRepository])
trait MetadataRepository {
  /**
   * Inserts or updates metadata.
   *
   * @param metadata Metadata to insert or update.
   * @return Identifier of the metadata.
   */
  def upsert(metadata: Metadata): Future[Id]

  /**
   * Gets the metadata.
   *
   * @return Metadata.
   */
  def get(): Future[Metadata]

}
