package sk.hrstka.services.impl.cache

import org.mockito.Matchers._
import org.mockito.Mockito._
import sk.hrstka.common.HrstkaCache
import sk.hrstka.models.domain.{CompRatingSpec, TechRatingSpec, UserSpec}
import sk.hrstka.repositories.{CompRepository, CompVoteRepository}
import sk.hrstka.services.impl.NotCachedCompService
import sk.hrstka.services.{CompService, LocationService, TechService}
import sk.hrstka.test.BaseSpec

import scala.concurrent.Future
import scala.reflect.ClassTag

class CachedCompServiceImplSpec extends BaseSpec {
  behavior of "upsert"

  it should "not cache underlying upsert" in new TestScope {
    verifyNoCaching(_.upsert(CompRatingSpec.avitech.comp, Set(TechRatingSpec.akkaRating.tech.handle), UserSpec.johny.id))
  }

  it should "not cache underlying voteUp" in new TestScope {
    verifyNoCaching(_.voteUp(CompRatingSpec.avitech.comp.businessNumber, UserSpec.johny.id))
  }

  it should "not cache underlying get" in new TestScope {
    verifyNoCaching(_.get(CompRatingSpec.avitech.comp.businessNumber))
  }

  it should "not cache underlying voteDown" in new TestScope {
    verifyNoCaching(_.voteDown(CompRatingSpec.avitech.comp.businessNumber, UserSpec.johny.id))
  }

  ignore should "cache underlying all" in new TestScope {
    verifyCaching(_.all(None, None))
  }

  private class TestScope {
    protected def verifyNoCaching(action: (CompService) => Any): Unit = {
      action(service)
      action(verify(underlying))
      verifyNoMoreInteractions(hrstkaCache)
      verifyNoMoreInteractions(underlying)
    }

    protected def verifyCaching[T : ClassTag](action: (CompService) => T): Unit = {
      action(service)
      verify(hrstkaCache)
      verifyNoMoreInteractions(hrstkaCache)
      verifyNoMoreInteractions(underlying)
    }

    val compRepository = mock[CompRepository]
    val compVoteRepository = mock[CompVoteRepository]
    val techService = mock[TechService]
    val locationService = mock[LocationService]
    val hrstkaCache = mock[HrstkaCache]
    val underlying = mock[NotCachedCompService]
    val service = new CachedCompServiceImpl(
      compRepository,
      compVoteRepository,
      techService,
      locationService,
      hrstkaCache,
      underlying)
  }
}
