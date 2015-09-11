package sk.hrstka.repositories.scripts.mongoDb.migration

import play.modules.reactivemongo.ReactiveMongoApi
import sk.hrstka.common.HrstkaException
import sk.hrstka.models.db.Metadata
import sk.hrstka.repositories.mongoDb.MongoMetadataRepository

import scala.concurrent.Future

private[mongoDb] trait MigrationScript {
  /**
   * Database version for which this migration script should be run.
   */
  def dbVersion: Int

  /**
   * Runs the migration script.
   */
  def run(reactiveMongoApi: ReactiveMongoApi,
          metadataRepository: MongoMetadataRepository): Future[Metadata]
}

private[mongoDb] object MigrationScript {
  val all: Seq[BaseMigrationScript] = Seq(
    V1MigrationScript,
    V2MigrationScript
  )

  def get(dbVersion: Int): MigrationScript = all.find(_.dbVersion == dbVersion) match {
    case Some(migrationScript) => migrationScript
    case None => throw new HrstkaException(s"No migration script for DB version! [$dbVersion]")
  }
}