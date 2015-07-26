package sk.hrstka.services.impl.cache

import sk.hrstka.models.domain.CitySpec
import sk.hrstka.repositories.{CityRepository, CompRepository}
import sk.hrstka.services.LocationService
import sk.hrstka.services.impl.NotCachedLocationService
import sk.hrstka.test.BaseSpec

class CachedLocationServiceImplSpec extends BaseSpec {
  behavior of "upsert"

  it should "not cache underlying upsert" in new TestScope {
    verifyNoCaching(_.upsert(CitySpec.bratislava))
  }

  behavior of "remove"

  it should "not cache underlying remove" in new TestScope {
    verifyNoCaching(_.remove(CitySpec.bratislava.handle))
  }

  behavior of "countries"

  it should "cache underlying countries" in new TestScope {
    verifyCaching(_.countries())
  }

  behavior of "getCountryByCode"

  it should "not cache underlying getCountryByCode" in new TestScope {
    verifyNoCaching(_.getCountryByCode(CitySpec.bratislava.country.code))
  }

  behavior of "cities"

  it should "cache underlying cities" in new TestScope {
    verifyCaching(_.usedCities())
  }

  behavior of "city"

  it should "not cache underlying city" in new TestScope {
    verifyNoCaching(_.city(CitySpec.bratislava.handle))
  }

  private class TestScope extends CacheTestScope[LocationService] {
    val cityRepository = mock[CityRepository]
    val compRepository = mock[CompRepository]
    override val underlying = mock[NotCachedLocationService]
    override val service = new CachedLocationServiceImpl(
      hrstkaCache,
      cityRepository,
      compRepository,
      underlying
    )
  }
}
