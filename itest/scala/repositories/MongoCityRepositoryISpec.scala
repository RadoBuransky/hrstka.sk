package repositories

import itest.TestApplication
import models.db.{Identifiable, City}
import models.domain.Handle
import org.scalatest.DoNotDiscover
import reactivemongo.bson.BSONObjectID
import reactivemongo.core.errors.DatabaseException
import repositories.mongoDb.CityCollection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@DoNotDiscover
final class MongoCityRepositoryISpec(testApplication: TestApplication)
  extends BaseRepositoryISpec[CityRepository](testApplication, CityCollection) {
  import MongoCityRepositoryISpec._

  // TODO: Test base repo ...

//  behavior of "all"
//
//  it should "get all inserted cities" in { cityRepository =>
//    val result = insertAll(cityRepository).flatMap { _ =>
//      cityRepository.all()
//    }
//    assert(result.futureValue.toSet == Set(kosice, noveZamky))
//  }
//
//  behavior of "get"
//
//  it should "return inserted city" in { cityRepository =>
//    val result = cityRepository.insert(noveZamky).flatMap { _ =>
//      cityRepository.getByHandle(noveZamky.handle)
//    }
//    assert(result.futureValue == noveZamky)
//  }
//
//  behavior of "find"
//
//  it should "find one city" in { cityRepository =>
//    val result = insertAll(cityRepository).flatMap { _ =>
//      cityRepository.findByHandle(kosice.handle)
//    }
//    assert(result.futureValue.contains(kosice))
//  }
//
//  it should "find no city" in { cityRepository =>
//    val result = insertAll(cityRepository).flatMap { _ =>
//      cityRepository.findByHandle("a")
//    }
//    assert(result.futureValue.isEmpty)
//  }
//
//  behavior of "insert"
//
//  it should "not allow to insert the same city" in { cityRepository =>
//    val result = cityRepository.insert(kosice).flatMap { _ =>
//      cityRepository.insert(kosice.copy(_id = BSONObjectID.generate))
//    }
//    whenReady(result.failed) { ex =>
//      assert(ex.isInstanceOf[DatabaseException])
//    }
//  }
//
//  private def insertAll(cityRepository: CityRepository): Future[_] = for {
//      kosiceFuture <- cityRepository.insert(kosice)
//      noveZamkyFuture <- cityRepository.insert(noveZamky)
//    } yield ()
}

private object MongoCityRepositoryISpec {
  val kosice = createCity("Košice")
  val noveZamky = createCity("Nové Zámky")

  private def createCity(sk: String) = City(
    _id     = Identifiable.empty,
    handle  = Handle.fromHumanName(sk).value,
    sk      = sk
  )
}