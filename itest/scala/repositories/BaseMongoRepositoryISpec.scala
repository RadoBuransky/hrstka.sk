package repositories

import _root_.itest.TestApplication
import common.HEException
import models.db.{City, Identifiable}
import models.domain.Handle
import org.scalatest.{DoNotDiscover, Suites}
import reactivemongo.bson.BSONObjectID
import repositories.itest.BaseRepositoryISpec
import repositories.mongoDb.{CityCollection, MongoCityRepository}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class StandaloneBaseMongoRepositoryISpec extends Suites with TestApplication {
  override val nestedSuites = Vector(new BaseMongoRepositoryISpec(this))
}

@DoNotDiscover
class BaseMongoRepositoryISpec(testApplication: TestApplication)
  extends BaseRepositoryISpec[MongoCityRepository](testApplication, CityCollection) {
  import BaseMongoRepositoryISpec._

  behavior of "insert"

  it should "fail if _id is already set" in { baseMongoRepository =>
    intercept[HEException] { baseMongoRepository.insert(kosice.copy(_id = BSONObjectID.generate)) }
  }

  behavior of "insert and get"

  it should "generate _id and insert entity" in { baseMongoRepository =>
    val result = baseMongoRepository.insert(kosice)
    val id = result.futureValue
    assert(id != Identifiable.empty)
    assert(baseMongoRepository.get(id).futureValue.copy(_id = Identifiable.empty) == kosice)
  }

  behavior of "upsert"

  it should "insert entity if _id is not set" in { baseMongoRepository =>
    val result = baseMongoRepository.upsert(kosice)
    val id = result.futureValue
    assert(id != Identifiable.empty)
    assert(baseMongoRepository.get(id).futureValue.copy(_id = Identifiable.empty) == kosice)
  }

  it should "update entity if _id is set" in { baseMongoRepository =>
    val result = baseMongoRepository.upsert(kosice).flatMap { kosiceId =>
      baseMongoRepository.upsert(kosice.copy(
        _id = kosiceId,
        sk  = "abc"
      ))
    }
    val id = result.futureValue
    assert(id != Identifiable.empty)

    val updatedCity = baseMongoRepository.get(id).futureValue
    assert(updatedCity._id == id)
    assert(updatedCity.handle == kosice.handle)
    assert(updatedCity.sk == "abc")
  }

  behavior of "getByHandle"

  it should "fail if entity with such handle does not exist" in { baseMongoRepository =>
    val result = baseMongoRepository.getByHandle("x")
    whenReady(result.failed) { ex =>
      assert(ex.isInstanceOf[HEException])
    }
  }

  it should "return entity for the handle" in { baseMongoRepository =>
    val result = insertAll(baseMongoRepository).flatMap { _ =>
      baseMongoRepository.getByHandle(kosice.handle)
    }
    assert(result.futureValue.copy(_id = Identifiable.empty) == kosice)
  }

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
  private def insertAll(cityRepository: CityRepository): Future[_] = for {
      kosiceFuture <- cityRepository.insert(kosice)
      noveZamkyFuture <- cityRepository.insert(noveZamky)
    } yield ()
}

private object BaseMongoRepositoryISpec {
  val kosice = createCity("Košice")
  val noveZamky = createCity("Nové Zámky")

  private def createCity(sk: String) = City(
    _id     = Identifiable.empty,
    handle  = Handle.fromHumanName(sk).value,
    sk      = sk
  )
}
