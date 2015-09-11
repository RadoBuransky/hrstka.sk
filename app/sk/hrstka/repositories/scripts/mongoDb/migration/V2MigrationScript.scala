package sk.hrstka.repositories.scripts.mongoDb.migration

import play.api.libs.json.{JsArray, JsObject, Json}
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json.collection.JSONCollection
import sk.hrstka.common.Logging
import sk.hrstka.models.db.Metadata
import sk.hrstka.repositories.mongoDb.{CompCollection, MongoMetadataRepository}
import sk.hrstka.models.db.JsonFormats._
import play.modules.reactivemongo.json._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object V2MigrationScript extends BaseMigrationScript with Logging {
  override val dbVersion = 2
  override def run(reactiveMongoApi: ReactiveMongoApi,
                   metadataRepository: MongoMetadataRepository): Future[Metadata] = {
    super.migrate(reactiveMongoApi, metadataRepository) { () =>
      val compCollection = reactiveMongoApi.db.collection[JSONCollection](CompCollection.name)

      // Read all companies
      compCollection.find(Json.obj()).cursor[JsObject]().collect[Seq]().flatMap { oldComps =>
        // Drop the collection
        compCollection.drop().flatMap { _ =>
          // Migrate all companies
          val allSaves = oldComps.map { oldComp =>
            val newCompWithCity = oldComp + ("cities" -> JsArray(Seq((oldComp \ "city").get)))
            val newComp = newCompWithCity - "city"

            // Insert new company to temporary collection
            compCollection.save(newComp)
          }

          Future.sequence(allSaves)
        }
      }
    }
  }
}
