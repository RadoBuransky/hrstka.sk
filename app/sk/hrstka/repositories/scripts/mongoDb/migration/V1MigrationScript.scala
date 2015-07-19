package sk.hrstka.repositories.scripts.mongoDb.migration

import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json.collection.JSONCollection
import sk.hrstka.common.Logging
import sk.hrstka.models.db.Metadata
import sk.hrstka.models.domain.Slovakia
import sk.hrstka.repositories.mongoDb.{CityCollection, MongoMetadataRepository}
import sk.hrstka.models.db.JsonFormats._
import play.modules.reactivemongo.json._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object V1MigrationScript extends BaseMigrationScript with Logging {
  override val dbVersion = 1
  override def run(reactiveMongoApi: ReactiveMongoApi,
                   metadataRepository: MongoMetadataRepository): Future[Metadata] = {
    super.migrate(reactiveMongoApi, metadataRepository) { () =>
      val cityCollection = reactiveMongoApi.db.collection[JSONCollection](CityCollection.name)

      cityCollection.update(
        selector  = Json.obj(),
        update    = Json.obj(
          // Rename city sk field to en
          "$rename" -> Json.obj("sk" -> "en"),
          // Set country code to Slovakia
          "$set"    -> Json.obj("countryCode" -> Slovakia.code.value)
        )
      ).map(_ => ())
    }
  }
}
