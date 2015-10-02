package sk.hrstka.repositories.mongoDb.cache

import com.google.inject.{Inject, Singleton}
import sk.hrstka.common.HrstkaCache
import sk.hrstka.models.db.Comp
import sk.hrstka.models.db.Identifiable.Id
import sk.hrstka.repositories.CompRepository
import sk.hrstka.repositories.mongoDb.NotCachedCompRepository

import scala.concurrent.Future

@Singleton
class CachedMongoCompRepository @Inject() (hrstkaCache: HrstkaCache,
                                           underlying: NotCachedCompRepository)
  extends CompRepository {
  override def upsert(comp: Comp): Future[Id] = underlying.upsert(comp)
  override def get(businessNumber: String): Future[Comp] = underlying.get(businessNumber)
  override def all(): Future[Iterable[Comp]] = hrstkaCache.cacheSuccess("CachedMongoCompRepository.all()", underlying.all())
}