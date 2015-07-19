package sk.hrstka.repositories.scripts.mongoDb.migration

import play.modules.reactivemongo.ReactiveMongoApi
import sk.hrstka.common.Logging
import sk.hrstka.models.db.Metadata
import sk.hrstka.repositories.mongoDb.MongoMetadataRepository

import scala.concurrent.Future

object V1MigrationScript extends BaseMigrationScript with Logging {
  override val dbVersion = 1
  override def run(reactiveMongoApi: ReactiveMongoApi,
                   metadataRepository: MongoMetadataRepository): Future[Metadata] = {
    super.migrate(reactiveMongoApi, metadataRepository) { () =>
      Future.successful()
    }
  }
}
