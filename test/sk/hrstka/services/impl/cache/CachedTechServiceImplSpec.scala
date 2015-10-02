package sk.hrstka.services.impl.cache

import sk.hrstka.models.domain.{TechRatingSpec, UserSpec}
import sk.hrstka.repositories.{CompRepository, TechRepository, TechVoteRepository}
import sk.hrstka.services.TechService
import sk.hrstka.services.impl.NotCachedTechService
import sk.hrstka.test.BaseSpec

class CachedTechServiceImplSpec extends BaseSpec {
  behavior of "upsert"

  it should "not cache underlying upsert" in new TestScope {
    verifyNoCaching(_.upsert(TechRatingSpec.akkaRating.tech))
  }

  behavior of "voteUp"

  it should "not cache underlying voteUp" in new TestScope {
    verifyNoCaching(_.voteUp(TechRatingSpec.akkaRating.tech.handle, UserSpec.rado.id))
  }

  behavior of "getByHandle"

  it should "not cache underlying getByHandle" in new TestScope {
    verifyNoCaching(_.getByHandle(TechRatingSpec.akkaRating.tech.handle))
  }

  behavior of "voteDown"

  it should "not cache underlying voteDown" in new TestScope {
    verifyNoCaching(_.voteDown(TechRatingSpec.akkaRating.tech.handle, UserSpec.rado.id))
  }

  behavior of "allCategories"

  it should "cache underlying allCategories" in new TestScope {
    verifyCaching(_.allCategories())
  }

  behavior of "allRatings"

  it should "cache underlying allRatings" in new TestScope {
    verifyCaching(_.allRatings())
  }

  behavior of "remove"

  it should "not cache underlying remove" in new TestScope {
    verifyNoCaching(_.remove(TechRatingSpec.akkaRating.tech.handle))
  }

  behavior of "votesFor"

  it should "not cache underlying votesFor" in new TestScope {
    verifyNoCaching(_.votesFor(UserSpec.rado.id))
  }

  private class TestScope extends CacheTestScope[TechService] {
    val techRepository = mock[TechRepository]
    val techVoteRepository = mock[TechVoteRepository]
    val compRepository = mock[CompRepository]
    override val underlying = mock[NotCachedTechService]
    override val service = new CachedTechServiceImpl(
      hrstkaCache,
      techRepository,
      techVoteRepository,
      compRepository,
      underlying
    )
  }
}
