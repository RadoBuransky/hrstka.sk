package sk.hrstka.repositories.mongoDb

import com.google.inject.{Inject, Singleton}
import play.modules.reactivemongo.ReactiveMongoApi
import sk.hrstka.models.db.Metadata
import sk.hrstka.repositories.MetadataRepository
import sk.hrstka.models.db.JsonFormats._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
final class MongoMetadataRepository @Inject() (protected val reactiveMongoApi: ReactiveMongoApi)
  extends BaseMongoRepository[Metadata](MetadataCollection) with MetadataRepository {
  override def get(): Future[Metadata] = all().map(_.head)
}
