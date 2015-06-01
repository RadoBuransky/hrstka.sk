package repositories.scripts.mongoDb

import com.google.inject.{Inject, Singleton}
import common.HEException
import play.api.Logger
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.DBMetaCommands
import reactivemongo.api.indexes.IndexType.Ascending
import reactivemongo.api.indexes.{Index, NSIndex}
import repositories.mongoDb._
import repositories.scripts.DbManager

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class MongoDbManager @Inject() (reactiveMongoApi: ReactiveMongoApi) extends DbManager {
  private val logger = Logger(getClass)

  import MongoDbManager._
  override def applicationInit(): Future[_] = {
    reactiveMongoApi.db match {
      case dbMetaCommands: DBMetaCommands => applicationInit(dbMetaCommands)
      case _ => Future.failed(new HEException("Not a DBMetaCommands instance!"))
    }
  }

  private def applicationInit(dBMetaCommands: DBMetaCommands): Future[_] = {
    logger.info(s"Initializing MongoDB [${reactiveMongoApi.db.name}].")

    // Ensure all indexes exist
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
  lazy val allIndexes = Seq(
    compNameIndex,
    compWebsiteIndex,
    techNameIndex,
    userEmailIndex,
    cityHandleIndex
  )

  lazy val compNameIndex = createUniqueIndex(CompCollection, "name")
  lazy val compWebsiteIndex = createUniqueIndex(CompCollection, "website")
  lazy val techNameIndex = createUniqueIndex(TechCollection, "name")
  lazy val userEmailIndex = createUniqueIndex(UserCollection, "email")
  lazy val cityHandleIndex = createUniqueIndex(CityCollection, "handle")

  private def createUniqueIndex(coll: MongoCollection, fieldName: String): NSIndex = NSIndex("db." + coll.name,
    Index(
      key     = Seq(fieldName -> Ascending),
      unique  = true
    ))
}
