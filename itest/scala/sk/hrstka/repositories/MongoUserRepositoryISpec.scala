package sk.hrstka.repositories

import _root_.itest.TestApplication
import sk.hrstka.common.HrstkaException
import org.scalatest.DoNotDiscover
import sk.hrstka.models.db.UserSpec
import sk.hrstka.repositories.itest.BaseRepositoryISpec
import sk.hrstka.repositories.mongoDb.{MongoUserRepository, UserCollection}

import scala.concurrent.ExecutionContext.Implicits.global

@DoNotDiscover
class MongoUserRepositoryISpec(testApplication: TestApplication)
  extends BaseRepositoryISpec[MongoUserRepository](testApplication, UserCollection) {
  import UserSpec._

  behavior of "upsert"

  it should "not allow to insert an user with the same email" in { userRepository =>
    val result = for {
      inserted1 <-userRepository.upsert(rado)
      inserted2 <-userRepository.upsert(johny.copy(email = rado.email))
    } yield inserted2

    whenReady(result.failed) { ex =>
      assert(ex.isInstanceOf[HrstkaException])
    }
  }
}