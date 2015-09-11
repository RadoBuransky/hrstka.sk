package sk.hrstka.repositories.scripts.mongoDb

import com.google.inject.{Inject, Singleton}
import play.api.Logger
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.DBMetaCommands
import reactivemongo.api.indexes.IndexType.Ascending
import reactivemongo.api.indexes.{Index, NSIndex}
import sk.hrstka.common.HrstkaException
import sk.hrstka.models.db.{Identifiable, Metadata}
import sk.hrstka.repositories.mongoDb._
import sk.hrstka.repositories.scripts.DbManager
import sk.hrstka.repositories.scripts.mongoDb.migration.MigrationScript

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class MongoDbManager @Inject() (reactiveMongoApi: ReactiveMongoApi,
                                metadataRepository: MongoMetadataRepository) extends DbManager {
  private val logger = Logger(getClass)

  override def applicationInit(): Future[Unit] = {
    reactiveMongoApi.db match {
      case dbMetaCommands: DBMetaCommands => applicationInit(dbMetaCommands)
      case _ => Future.failed(new HrstkaException("Not a DBMetaCommands instance!"))
    }
  }

  private def applicationInit(dBMetaCommands: DBMetaCommands): Future[Unit] = {
    logger.info(s"Initializing MongoDB [${reactiveMongoApi.db.name}].")

    for {
      metadata  <- ensureMetadata()
      _         = migrateDatabase(metadata)
      indexes   <- ensureIndexes(dBMetaCommands)
      result    = logger.info("Initialization of MongoDB done.")
    } yield result
  }

  private def migrateDatabase(metadata: Metadata): Future[Unit] = {
    logger.info(s"Database schema version ${metadata.dbVersion}.")
    logger.info(s"Application schema version ${MongoDbManager.dbVersion}.")
    if (metadata.dbVersion < MongoDbManager.dbVersion) {
      // Get the migration script and run it
      MigrationScript
        .get(metadata.dbVersion)
        .run(reactiveMongoApi, metadataRepository)
        .flatMap { newMetadata =>
          // Recursively upgrade
          migrateDatabase(newMetadata)
        }
    }
    else
      Future.successful(())
  }

  private def ensureMetadata(): Future[Metadata] = {
    metadataRepository.get().recoverWith {
      case _: NoSuchElementException =>
        Logger.info("Creating metadata.")

        // Create new metadata
        val metadata = Metadata(
          _id       = Identifiable.empty,
          dbVersion = MongoDbManager.dbVersion
        )

        // Insert to DB
        metadataRepository.insert(metadata).map { _ =>
          Logger.info("Metadata inserted.")
          metadata
        }

      case ex: Throwable => throw new HrstkaException(s"Cannot get metadata!", ex)
    }
  }

  private def ensureIndexes(dBMetaCommands: DBMetaCommands): Future[Unit] = {
    val allIndexes = MongoDbManager.allIndexes(reactiveMongoApi.db.name)
    Future.sequence(allIndexes.map(dBMetaCommands.indexesManager.ensure)).map { indexes =>
      val createdIndexes = indexes.zipWithIndex.filter(_._1)
      createdIndexes.foreach { createdIndex =>
        val index = allIndexes(createdIndex._2)
        logger.info(s"Index created. [${index.collectionName} ${index.index.key.map(_._1).mkString("{ ",", "," }")}]")
      }
      if (createdIndexes.isEmpty)
        logger.info("No indexes created.")

    }
  }
}

private object MongoDbManager {
  val dbVersion = MigrationScript.all.map(_.dbVersion).max + 1

  def allIndexes(dbName: String) = Seq(
    compNameIndex(dbName),
    compWebsiteIndex(dbName),
    compBusinessNumberIndex(dbName),
    techHandleIndex(dbName),
    userEmailIndex(dbName),
    cityHandleIndex(dbName),
    techVoteUserTechIndex(dbName),
    compVoteUserTechIndex(dbName)
  )

  def compNameIndex(dbName: String) = createUniqueIndex(dbName, CompCollection, "name")
  def compWebsiteIndex(dbName: String) = createUniqueIndex(dbName, CompCollection, "website")
  def compBusinessNumberIndex(dbName: String) = createUniqueIndex(dbName, CompCollection, "businessNumber")
  def techHandleIndex(dbName: String) = createUniqueIndex(dbName, TechCollection, "handle")
  def userEmailIndex(dbName: String) = createUniqueIndex(dbName, UserCollection, "email")
  def cityHandleIndex(dbName: String) = createUniqueIndex(dbName, CityCollection, "handle")
  def techVoteUserTechIndex(dbName: String) = createUniqueIndex(dbName, TechVoteCollection, "userId", "entityId")
  def compVoteUserTechIndex(dbName: String) = createUniqueIndex(dbName, CompVoteCollection, "userId", "entityId")

  private def createUniqueIndex(dbName: String, coll: MongoCollection, fieldNames: String*) = NSIndex(dbName + "." + coll.name,
    Index(
      key     = fieldNames.map(_ -> Ascending),
      unique  = true
    ))
}
