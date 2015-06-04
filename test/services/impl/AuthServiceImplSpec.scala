package services.impl

import models.db
import org.mockito.ArgumentCaptor
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import reactivemongo.bson.BSONObjectID
import repositories.UserRepository

import scala.concurrent.Future

class AuthServiceImplSpec extends FlatSpec with MockitoSugar {
  import models.db.UserSpec._

  behavior of "createUser"

  it should "insert eminent with an encrypted password to DB" in new TestScope {
    // Prepare
    val id = BSONObjectID.generate
    val email = "a@a.com"
    val password = "123"
    when(userRepository.insert(any[db.User])).thenReturn(Future.successful(id))

    // Execute
    authService.createUser(email, password)

    // Verify
    val userCaptor = ArgumentCaptor.forClass(classOf[db.User])
    verify(userRepository).insert(userCaptor.capture())
    val user = userCaptor.getValue

    // Assert
    assert(user._id == db.Identifiable.empty)
    assert(user.email == email)
    assert(user.encryptedPassword.nonEmpty)
    assert(user.encryptedPassword != password)
  }

  behavior of "findByEmail"

  it should "find an user in DB" in new TestScope {
    // Prepare
    when(userRepository.findByEmail("a@a.com")).thenReturn(Future.successful(Some(rado)))

    // Execute
    authService.findByEmail("a@a.com")
  }

  private class TestScope {
    val userRepository = mock[UserRepository]
    val authService = new AuthServiceImpl(userRepository)
  }
}
