package sk.hrstka.repositories

import org.scalatest.DoNotDiscover
import sk.hrstka.common.HrstkaException
import sk.hrstka.itest.TestApplication
import sk.hrstka.models.db.CompSpec
import sk.hrstka.repositories.itest.BaseRepositoryISpec
import sk.hrstka.repositories.mongoDb.{CompCollection, MongoCompRepository}

import scala.concurrent.ExecutionContext.Implicits.global

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

  it should "not allow to insert a company with the same business number" in { compRepository =>
    val result = for {
      inserted1 <-compRepository.upsert(avitech)
      inserted2 <-compRepository.upsert(borci.copy(businessNumber = avitech.businessNumber))
    } yield inserted2

    whenReady(result.failed) { ex =>
      assert(ex.isInstanceOf[HrstkaException])
    }
  }
}