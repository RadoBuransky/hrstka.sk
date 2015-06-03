package repositories

import _root_.itest.TestApplication
import common.HEException
import models.db.{Identifiable, User}
import org.scalatest.DoNotDiscover
import repositories.itest.BaseRepositoryISpec
import repositories.mongoDb.{MongoUserRepository, UserCollection}

import scala.concurrent.ExecutionContext.Implicits.global

@DoNotDiscover
class MongoUserRepositoryISpec(testApplication: TestApplication)
  extends BaseRepositoryISpec[MongoUserRepository](testApplication, UserCollection) {
  import MongoUserRepositoryISpec._

  behavior of "upsert"

  it should "not allow to insert an user with the same email" in { userRepository =>
    val result = for {
      inserted1 <-userRepository.upsert(rado)
      inserted2 <-userRepository.upsert(johny.copy(email = rado.email))
    } yield inserted2

    whenReady(result.failed) { ex =>
      assert(ex.isInstanceOf[HEException])
    }
  }
}

object MongoUserRepositoryISpec {
  val rado = User(
    _id               = Identifiable.empty,
    email             = "radoburansky@gmail.com",
    encryptedPassword = "abc",
    role              = "slave"
  )

  val johny = User(
    _id               = Identifiable.empty,
    email             = "johny@noidea.com",
    encryptedPassword = "xxx",
    role              = "master"
  )
}