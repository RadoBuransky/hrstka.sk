package sk.hrstka.repositories.mongoDb

import com.google.inject.{Inject, Singleton}
import play.modules.reactivemongo.ReactiveMongoApi
import sk.hrstka.common.HrstkaCache
import sk.hrstka.models.db.City
import sk.hrstka.models.db.JsonFormats._
import sk.hrstka.repositories.CityRepository

@Singleton
final class MongoCityRepository @Inject() (hrstkaCache: HrstkaCache,
                                           protected val reactiveMongoApi: ReactiveMongoApi)
  extends BaseMongoRepository[City](CityCollection) with CityRepository {
  override def insert(city: City) = hrstkaCache.invalidateOnSuccess(super.insert(city))}
