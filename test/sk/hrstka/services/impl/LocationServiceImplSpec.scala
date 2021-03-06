package sk.hrstka.services.impl

import org.mockito.Mockito._
import sk.hrstka.common.HrstkaException
import sk.hrstka.models.db
import sk.hrstka.models.domain._
import sk.hrstka.repositories.{CityRepository, CompRepository}
import sk.hrstka.test.BaseSpec

import scala.concurrent.Future

class LocationServiceImplSpec extends BaseSpec {
  behavior of "upsert"

  it should "insert city" in new TestScope {
    // Prepare
    val city = CitySpec.bratislava
    val dbCity = db.City(
      _id         = db.Identifiable.empty,
      handle      = city.handle.value,
      name          = city.name,
      countryCode = city.country.code.value
    )
    when(cityRepository.insert(dbCity)).thenReturn(Future.successful(dbCity._id))

    // Execute
    assert(locationService.upsert(city).futureValue == city.handle)

    // Verify
    verify(cityRepository).insert(dbCity)
    verifyNoMoreInteractions(cityRepository)
  }

  behavior of "countries"

  it should "return all countries ordered" in new TestScope {
    // Execute
    val expected = Seq(
      Slovakia,
      CzechRepublic,
      Austria,
      Hungary,
      Poland,
      Ukraine,
      Germany
    )
    assert(locationService.countries().futureValue == expected)
  }

  behavior of "getCountryByCode"

  it should "return Slovakia" in new TestScope {
    assert(locationService.getCountryByCode(Slovakia.code).futureValue == Slovakia)
  }

  it should "fail for an unknown country" in new TestScope {
    whenReady(locationService.getCountryByCode(Iso3166("XY")).failed) { ex =>
      assert(ex.isInstanceOf[HrstkaException])
      assert(ex.getMessage == "No country exists for the code! [XY]")
    }
  }

  behavior of "usedCities"

  it should "return ordered sequence of used cities" in new TestScope {
    // Prepare
    when(cityRepository.all())
      .thenReturn(Future.successful(db.CitySpec.all))
    when(compRepository.all())
      .thenReturn(Future.successful(db.CompSpec.all))

    // Execute
    val expected = Seq(CitySpec.bratislava, CitySpec.noveZamky)
    assert(locationService.usedCities().futureValue == expected)

    // Verify
    verify(compRepository).all()
    verify(cityRepository).all()
    verifyNoMoreInteractions(cityRepository)
  }

  behavior of "allCities"

  it should "return all cities unordered" in new TestScope {
    // Prepare
    when(cityRepository.all())
      .thenReturn(Future.successful(db.CitySpec.all))

    // Execute
    assert(locationService.allCities().futureValue == CitySpec.all)

    // Verify
    verify(cityRepository).all()
    verifyNoMoreInteractions(cityRepository)
  }

  behavior of "city"

  it should "return a city" in new TestScope {
    // Prepare
    when(cityRepository.getByHandle(db.CitySpec.noveZamky.handle))
      .thenReturn(Future.successful(db.CitySpec.noveZamky))

    // Execute
    assert(locationService.city(CitySpec.noveZamky.handle).futureValue == CitySpec.noveZamky)

    // Verify
    verify(cityRepository).getByHandle(db.CitySpec.noveZamky.handle)
    verifyNoMoreInteractions(cityRepository)
  }

  private class TestScope {
    val cityRepository = mock[CityRepository]
    val compRepository = mock[CompRepository]
    val locationService = new LocationServiceImpl(cityRepository, compRepository)
  }
}
