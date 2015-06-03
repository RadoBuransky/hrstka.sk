package repositories

import _root_.itest.TestApplication
import common.HrstkaException
import models.db.{City, Identifiable}
import models.domain.Handle
import org.scalatest.DoNotDiscover
import repositories.itest.BaseRepositoryISpec
import repositories.mongoDb.{CityCollection, MongoCityRepository}

import scala.concurrent.ExecutionContext.Implicits.global

@DoNotDiscover
class MongoCityRepositoryISpec(testApplication: TestApplication)
  extends BaseRepositoryISpec[MongoCityRepository](testApplication, CityCollection) {
  import MongoCityRepositoryISpec._

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

object MongoCityRepositoryISpec {
  val kosice = createCity("Košice")
  val noveZamky = createCity("Nové Zámky")

  private def createCity(sk: String) = City(
    _id     = Identifiable.empty,
    handle  = Handle.fromHumanName(sk).value,
    sk      = sk
  )
}
