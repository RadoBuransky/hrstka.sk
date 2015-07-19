package sk.hrstka.services.impl.cache

import sk.hrstka.models.domain.{CompRatingSpec, TechRatingSpec, UserSpec}
import sk.hrstka.repositories.{CompRepository, CompVoteRepository}
import sk.hrstka.services.impl.NotCachedCompService
import sk.hrstka.services.{CompService, LocationService, TechService}
import sk.hrstka.test.BaseSpec

class CachedCompServiceImplSpec extends BaseSpec {
  behavior of "upsert"

  it should "not cache underlying upsert" in new TestScope {
    verifyNoCaching(_.upsert(CompRatingSpec.avitech.comp, Set(TechRatingSpec.akkaRating.tech.handle), UserSpec.johny.id))
  }

  behavior of "voteUp"

  it should "not cache underlying voteUp" in new TestScope {
    verifyNoCaching(_.voteUp(CompRatingSpec.avitech.comp.businessNumber, UserSpec.johny.id))
  }

  behavior of "get"

  it should "not cache underlying get" in new TestScope {
    verifyNoCaching(_.get(CompRatingSpec.avitech.comp.businessNumber))
  }

  behavior of "voteDown"

  it should "not cache underlying voteDown" in new TestScope {
    verifyNoCaching(_.voteDown(CompRatingSpec.avitech.comp.businessNumber, UserSpec.johny.id))
  }

  behavior of "all"

  it should "cache underlying all" in new TestScope {
    verifyCaching(_.all(None, None))
  }

  private class TestScope extends CacheTestScope[CompService] {
    val compRepository = mock[CompRepository]
    val compVoteRepository = mock[CompVoteRepository]
    val techService = mock[TechService]
    val locationService = mock[LocationService]
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
