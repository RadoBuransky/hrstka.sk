package sk.hrstka.repositories

import org.scalatest.DoNotDiscover
import sk.hrstka.common.HrstkaException
import sk.hrstka.itest.TestApplication
import sk.hrstka.models.db.CitySpec
import sk.hrstka.repositories.itest.BaseRepositoryISpec
import sk.hrstka.repositories.mongoDb.{CityCollection, MongoCityRepository}

import scala.concurrent.ExecutionContext.Implicits.global

@DoNotDiscover
class MongoCityRepositoryISpec(testApplication: TestApplication)
  extends BaseRepositoryISpec[MongoCityRepository](testApplication, CityCollection) {
  import CitySpec._

  behavior of "upsert"

  it should "not allow to insert a city with the same handle" in { cityRepository =>
    val result = for {
      inserted1 <-cityRepository.upsert(kosice)
      inserted2 <-cityRepository.upsert(noveZamky.copy(handle = kosice.handle))
    } yield inserted2

    whenReady(result.failed) { ex =>
      assert(ex.isInstanceOf[HrstkaException])
    }
  }
}