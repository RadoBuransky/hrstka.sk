package services.impl

import models.{db, domain}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.FlatSpec
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import reactivemongo.bson.BSONObjectID
import repositories.UserRepository

import scala.concurrent.Future

class AuthServiceImplSpec extends FlatSpec with MockitoSugar with ScalaFutures {
  import models.db.UserSpec._

  behavior of "createUser"

  it should "insert eminent with an encrypted password to DB" in new TestScope {
    // Prepare
    val id = BSONObjectID.generate
    val email = rado.email
    val password = radoPassword
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

  it should "find an user in DB and map it to domain" in new TestScope {
    // Prepare
    when(userRepository.findByEmail(rado.email)).thenReturn(Future.successful(Some(rado)))

    // Execute
    assert(authService.findByEmail(rado.email).futureValue.contains(domain.User(rado)))
  }

  behavior of "authenticate"

  it should "return the user if exists and the password is correct" in new TestScope {
    // Prepare
    when(userRepository.findByEmail(rado.email)).thenReturn(Future.successful(Some(rado)))

    // Execute
    assert(authService.authenticate(rado.email, radoPassword).futureValue.contains(domain.User(rado)))
  }

  it should "return none if user exists but the password is wrong" in new TestScope {
    // Prepare
    when(userRepository.findByEmail(rado.email)).thenReturn(Future.successful(Some(rado)))

    // Execute
    assert(authService.authenticate(rado.email, "123").futureValue.isEmpty)
  }

  it should "return none if user does not exist" in new TestScope {
    // Prepare
    when(userRepository.findByEmail(rado.email)).thenReturn(Future.successful(None))

    // Execute
    assert(authService.authenticate(rado.email, radoPassword).futureValue.isEmpty)
  }

  private class TestScope {
    val userRepository = mock[UserRepository]
    val authService = new AuthServiceImpl(userRepository)
  }
}
