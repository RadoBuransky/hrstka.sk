package sk.hrstka.services.impl

import org.mockito.Mockito._
import sk.hrstka.models.db
import sk.hrstka.models.domain.CitySpec
import sk.hrstka.repositories.{CompRepository, CityRepository}
import sk.hrstka.test.BaseSpec

import scala.concurrent.Future

class LocationServiceImplSpec extends BaseSpec {
  behavior of "all"

  it should "return all cities" in new TestScope {
    // Prepare
    when(cityRepository.all())
      .thenReturn(Future.successful(db.CitySpec.all))
    when(compRepository.all(None, None))
      .thenReturn(Future.successful(db.CompSpec.all))

    // Execute
    val expected = CitySpec.all.sortBy(city => -1 * db.CompSpec.all.count(_.city == city.handle.value))
    assert(locationService.all().futureValue == expected)

    // Verify
    verify(compRepository).all(None, None)
    verify(cityRepository).all()
    verifyNoMoreInteractions(cityRepository)
  }

  behavior of "get"

  it should "return a city" in new TestScope {
    // Prepare
    when(cityRepository.getByHandle(db.CitySpec.noveZamky.handle))
      .thenReturn(Future.successful(db.CitySpec.noveZamky))

    // Execute
    assert(locationService.get(CitySpec.noveZamky.handle).futureValue == CitySpec.noveZamky)

    // Verify
    verify(cityRepository).getByHandle(db.CitySpec.noveZamky.handle)
    verifyNoMoreInteractions(cityRepository)
  }

  behavior of "getOrCreate"

  it should "get a city if already exists" in new TestScope {
    // Prepare
    when(cityRepository.findByHandle(db.CitySpec.kosice.handle))
      .thenReturn(Future.successful(Some(db.CitySpec.kosice)))

    // Execute
    assert(locationService.getOrCreateCity(CitySpec.kosice.sk).futureValue == CitySpec.kosice)

    // Verify
    verify(cityRepository).findByHandle(db.CitySpec.kosice.handle)
    verifyNoMoreInteractions(cityRepository)
  }

  it should "create a city if doesn't exist yet" in new TestScope {
    // Prepare
    when(cityRepository.findByHandle(db.CitySpec.kosice.handle))
      .thenReturn(Future.successful(None))

    // Execute
    assert(locationService.getOrCreateCity(CitySpec.kosice.sk).futureValue == CitySpec.kosice)

    // Verify
    verify(cityRepository).findByHandle(db.CitySpec.kosice.handle)
    verify(cityRepository).insert(db.CitySpec.kosice)
    verifyNoMoreInteractions(cityRepository)
  }

  private class TestScope {
    val cityRepository = mock[CityRepository]
    val compRepository = mock[CompRepository]
    val locationService = new LocationServiceImpl(cityRepository, compRepository)
  }
}
