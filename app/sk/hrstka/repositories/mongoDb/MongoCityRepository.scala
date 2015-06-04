package sk.hrstka.repositories.mongoDb

import com.google.inject.{Inject, Singleton}
import play.modules.reactivemongo.ReactiveMongoApi
import sk.hrstka.models.db.JsonFormats._
import sk.hrstka.models.db.{City, JsonFormats}
import sk.hrstka.repositories.CityRepository

@Singleton
final class MongoCityRepository @Inject() (protected val reactiveMongoApi: ReactiveMongoApi)
  extends BaseMongoRepository[City](CityCollection) with CityRepository
