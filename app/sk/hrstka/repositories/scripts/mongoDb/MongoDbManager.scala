package sk.hrstka.repositories.scripts.mongoDb

import com.google.inject.{Inject, Singleton}
import play.api.Logger
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.DBMetaCommands
import reactivemongo.api.indexes.IndexType.Ascending
import reactivemongo.api.indexes.{Index, NSIndex}
import sk.hrstka.common.HrstkaException
import sk.hrstka.repositories.mongoDb._
import sk.hrstka.repositories.scripts.DbManager

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class MongoDbManager @Inject() (reactiveMongoApi: ReactiveMongoApi) extends DbManager {
  private val logger = Logger(getClass)

  override def applicationInit(): Future[Unit] = {
    reactiveMongoApi.db match {
      case dbMetaCommands: DBMetaCommands => applicationInit(dbMetaCommands)
      case _ => Future.failed(new HrstkaException("Not a DBMetaCommands instance!"))
    }
  }

  private def applicationInit(dBMetaCommands: DBMetaCommands): Future[Unit] = {
    logger.info(s"Initializing MongoDB [${reactiveMongoApi.db.name}].")

    // Ensure all indexes exist
    val allIndexes = MongoDbManager.allIndexes(reactiveMongoApi.db.name)
    Future.sequence(allIndexes.map(dBMetaCommands.indexesManager.ensure)).map { indexes =>
      val createdIndexes = indexes.zipWithIndex.filter(_._1)
      createdIndexes.foreach { createdIndex =>
        val index = allIndexes(createdIndex._2)
        logger.info(s"Index created. [${index.collectionName} ${index.index.key.map(_._1).mkString("{ ",", "," }")}]")
      }
      if (createdIndexes.isEmpty)
        logger.info("No indexes created.")

      logger.info("Initialization of MongoDB done.")
    }
  }
}

private object MongoDbManager {
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
