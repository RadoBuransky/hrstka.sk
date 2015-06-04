package sk.hrstka.repositories.mongoDb

import com.google.inject.{Inject, Singleton}
import models.db.City
import models.db.JsonFormats._
import play.modules.reactivemongo.ReactiveMongoApi
import sk.hrstka.repositories.CityRepository

@Singleton
final class MongoCityRepository @Inject() (protected val reactiveMongoApi: ReactiveMongoApi)
  extends BaseMongoRepository[City](CityCollection) with CityRepository
