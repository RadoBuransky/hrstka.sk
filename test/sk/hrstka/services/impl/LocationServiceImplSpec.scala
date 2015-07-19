package sk.hrstka.services.impl

import org.mockito.Mockito._
import sk.hrstka.models.db
import sk.hrstka.models.domain._
import sk.hrstka.repositories.{CompRepository, CityRepository}
import sk.hrstka.test.BaseSpec

import scala.concurrent.Future

class LocationServiceImplSpec extends BaseSpec {
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

  behavior of "cities"

  it should "return all cities" in new TestScope {
    // Prepare
    when(cityRepository.all())
      .thenReturn(Future.successful(db.CitySpec.all))
    when(compRepository.all(None, None))
      .thenReturn(Future.successful(db.CompSpec.all))

    // Execute
    val expected = CitySpec.all.sortBy(city => -1 * db.CompSpec.all.count(_.city == city.handle.value))
    assert(locationService.cities().futureValue == expected)

    // Verify
    verify(compRepository).all(None, None)
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

  behavior of "getOrCreateCity"

  it should "get a city if already exists" in new TestScope {
    // Prepare
    when(cityRepository.findByHandle(db.CitySpec.kosice.handle))
      .thenReturn(Future.successful(Some(db.CitySpec.kosice)))

    // Execute
    assert(locationService.getOrCreateCity(CitySpec.kosice.en).futureValue == CitySpec.kosice)

    // Verify
    verify(cityRepository).findByHandle(db.CitySpec.kosice.handle)
    verifyNoMoreInteractions(cityRepository)
  }

  private class TestScope {
    val cityRepository = mock[CityRepository]
    val compRepository = mock[CompRepository]
    val locationService = new LocationServiceImpl(cityRepository, compRepository)
  }
}
