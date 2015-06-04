package sk.hrstka.repositories

import _root_.itest.TestApplication
import sk.hrstka.common.HrstkaException
import org.scalatest.DoNotDiscover
import sk.hrstka.models.db.CompSpec
import sk.hrstka.repositories.itest.BaseRepositoryISpec
import sk.hrstka.repositories.mongoDb.{CompCollection, MongoCompRepository}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@DoNotDiscover
class MongoCompRepositoryISpec(testApplication: TestApplication)
  extends BaseRepositoryISpec[MongoCompRepository](testApplication, CompCollection) {
  import CompSpec._

  behavior of "upsert"

  it should "not allow to insert a company with the same name" in { compRepository =>
    val result = for {
      inserted1 <-compRepository.upsert(avitech)
      inserted2 <-compRepository.upsert(borci.copy(name = avitech.name))
    } yield inserted2

    whenReady(result.failed) { ex =>
      assert(ex.isInstanceOf[HrstkaException])
    }
  }

  it should "not allow to insert a company with the same website" in { compRepository =>
    val result = for {
      inserted1 <-compRepository.upsert(avitech)
      inserted2 <-compRepository.upsert(borci.copy(website = avitech.website))
    } yield inserted2

    whenReady(result.failed) { ex =>
      assert(ex.isInstanceOf[HrstkaException])
    }
  }

  behavior of "all"

  it should "return everything if no filtering is used" in { compRepository =>
    val result = insertComps(compRepository).flatMap { _ =>
      compRepository.all()
    }
    assert(result.futureValue.toSet == Set(avitech, borci))
  }

  it should "return companies for a city" in { compRepository =>
    val result = insertComps(compRepository).flatMap { _ =>
      compRepository.all(city = Some(avitech.city))
    }
    assert(result.futureValue == Seq(avitech))
  }

  it should "return companies for a tech" in { compRepository =>
    val result = insertComps(compRepository).flatMap { _ =>
      compRepository.all(tech = borci.techs.headOption)
    }
    assert(result.futureValue == Seq(borci))
  }

  private def insertComps(compRepository: CompRepository): Future[_] = {
    for {
      avitechFuture <- compRepository.upsert(avitech)
      rescoFuture <- compRepository.upsert(borci)
    } yield ()
  }
}