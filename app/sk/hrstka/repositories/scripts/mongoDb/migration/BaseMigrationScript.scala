package sk.hrstka.repositories.scripts.mongoDb.migration

import play.api.Logger
import play.modules.reactivemongo.ReactiveMongoApi
import sk.hrstka.common.HrstkaException
import sk.hrstka.models.db.Metadata
import sk.hrstka.repositories.mongoDb.MongoMetadataRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

private[migration] abstract class BaseMigrationScript extends MigrationScript {
  protected def migrate(reactiveMongoApi: ReactiveMongoApi,
                        metadataRepository: MongoMetadataRepository)(migration: () => Future[Any]): Future[Metadata] = {
    // Get current metadata
    metadataRepository.get().flatMap { metadata =>
      if (metadata.dbVersion == dbVersion) {
        // Run the migration
        Logger.info(s"Upgrading database from version ${metadata.dbVersion}...")
        val migrationResult = migration()
        migrationResult.onFailure {
          case t: Throwable => throw new HrstkaException(s"Migration script for version ${metadata.dbVersion} failed!", t)
        }

        migrationResult.flatMap { _ =>
          // Update metadata
          val nextMetadata = metadata.copy(dbVersion = metadata.dbVersion + 1)
          val result = metadataRepository.upsert(nextMetadata)

          result.onComplete { _ =>
            Logger.info(s"Database upgraded to version ${nextMetadata.dbVersion}.")
          }

          result.map(_ => nextMetadata)
        }
      }
      else {
        Logger.debug(s"Current database version does not match version of this script. [${metadata.dbVersion}, $dbVersion]")
        Future.successful(metadata)
      }
    }
  }
}
