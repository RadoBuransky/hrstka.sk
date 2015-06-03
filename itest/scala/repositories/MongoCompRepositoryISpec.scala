package repositories

import java.net.URL

import _root_.itest.TestApplication
import common.HEException
import models.db.{Comp, Identifiable}
import org.scalatest.DoNotDiscover
import reactivemongo.bson.BSONObjectID
import repositories.itest.BaseRepositoryISpec
import repositories.mongoDb.{CompCollection, MongoCompRepository}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@DoNotDiscover
class MongoCompRepositoryISpec(testApplication: TestApplication)
  extends BaseRepositoryISpec[MongoCompRepository](testApplication, CompCollection) {
  import MongoCompRepositoryISpec._
  behavior of "all"

  it should "return everything if no filtering is used" in { compRepository =>
    val result = insertComps(compRepository).flatMap { _ =>
      compRepository.all()
    }
    assert(result.futureValue.toSet == Set(avitech, resco))
  }

  it should "return companies for a city" in { compRepository =>
    val result = insertComps(compRepository).flatMap { _ =>
      compRepository.all(city = Some(avitech.city))
    }
    assert(result.futureValue == Seq(avitech))
  }

  it should "return companies for a tech" in { compRepository =>
    val result = insertComps(compRepository).flatMap { _ =>
      compRepository.all(tech = resco.techs.headOption)
    }
    assert(result.futureValue == Seq(resco))
  }

  private def insertComps(compRepository: CompRepository): Future[_] = {
    for {
      avitechFuture <- compRepository.upsert(avitech)
      rescoFuture <- compRepository.upsert(resco)
    } yield ()
  }
}

object MongoCompRepositoryISpec {
  lazy val avitech = Comp(
    _id               = BSONObjectID.generate,
    authorId          = BSONObjectID.generate,
    name              = "Avitech",
    website           = new URL("http://avitech.aero/").toString,
    city              = "bratislava",
    employeeCount     = Some(60),
    codersCount       = Some(30),
    femaleCodersCount = Some(5),
    note              = "note",
    products          = true,
    services          = true,
    internal          = false,
    techs             = Seq("scala", "java"),
    joel              = Set(3, 5, 7)
  )

  lazy val resco = Comp(
    _id               = BSONObjectID.generate,
    authorId          = BSONObjectID.generate,
    name              = "Resco",
    website           = new URL("http://resco.net/").toString,
    city              = "nitra",
    employeeCount     = Some(23),
    codersCount       = Some(23),
    femaleCodersCount = Some(5),
    note              = "",
    products          = true,
    services          = false,
    internal          = false,
    techs             = Seq("c#", "c++"),
    joel              = Set(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
  )
}