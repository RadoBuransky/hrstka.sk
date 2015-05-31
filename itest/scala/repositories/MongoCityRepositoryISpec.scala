package repositories

import itest.TestApplication
import models.db.City
import models.domain.Handle
import org.scalatest.concurrent.Futures
import org.scalatest.{Outcome, fixture}
import reactivemongo.bson.BSONObjectID
import reactivemongo.core.errors.DatabaseException

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

final class MongoCityRepositoryISpec extends fixture.FlatSpec with TestApplication with Futures {
  behavior of "insert"

  it should "insert city" in { insert(_, "Košice") }
  it should "not allow to insert the same city" in { cityRepository =>
    val f = insert(cityRepository, "Košice").flatMap(_ => insert(cityRepository, "Košice"))
    intercept[DatabaseException] { Await.result(f, 30.seconds) }
  }

  behavior of "get"

  it should "return inserted city" in { cityRepository =>
    val sk = "Nové Zámky"
    val city = City(
      _id     = BSONObjectID.generate,
      handle  = Handle.fromHumanName(sk).value,
      sk      = sk
    )

    val f = cityRepository.insert(city).flatMap { _ =>
      cityRepository.get(city.handle)
    }

    assert(Await.result(f, 30.seconds) == city)
  }

  private def insert(cityRepository: CityRepository, sk: String, _id: BSONObjectID = BSONObjectID.generate): Future[String] = {
    cityRepository.insert(City(
      _id     = _id,
      handle  = Handle.fromHumanName(sk).value,
      sk      = sk
    ))
  }

  override type FixtureParam = CityRepository
  override protected def withFixture(test: OneArgTest): Outcome = {
    test.apply(application.injector.instanceOf[CityRepository])
  }
}