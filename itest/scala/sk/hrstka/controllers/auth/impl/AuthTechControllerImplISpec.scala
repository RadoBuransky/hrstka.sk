package sk.hrstka.controllers.auth.impl

import java.net.URI

import org.mockito.Mockito._
import org.scalatest.DoNotDiscover
import play.api.Application
import play.api.test.Helpers._
import sk.hrstka.BaseStandaloneFakeApplicationSuites
import sk.hrstka.controllers.itest.BaseControllerISpec
import sk.hrstka.models.domain._

import scala.concurrent.Future

@DoNotDiscover
class StandaloneAuthTechControllerImplISpec extends BaseStandaloneFakeApplicationSuites {
  override val nestedSuites = Vector(new AuthTechControllerImplISpec(app))
}

@DoNotDiscover
class AuthTechControllerImplISpec(application: Application) extends BaseControllerISpec {
  behavior of "all"

  it should "not authorize anonymous user" in new TestScope {
    assertAnonymousUser(authTechController.all())
  }

  it should "get HTML view with all technologies" in new TestScope {
    withEminentUser() {
      // Prepare
      when(techService.allCategories())
        .thenReturn(Future.successful(TechCategory.allCategories))
      when(techService.votesFor(eminentUser.id))
        .thenReturn(Future.successful(TechVoteSpec.johnysVotes))
      when(techService.allRatings())
        .thenReturn(Future.successful(TechRatingSpec.allRatings))

      // Execute
      assertAuthView(eminentUser, authTechController, authTechController.all) { content =>
        assert(content.contains("<form action=\"/technology\" method=\"post\">"))
        assert(content.contains("<a href=\"http://www.scala-lang.org/\" target=\"_blank\">Scala</a>"))
        assert(content.contains("<a href=\"https://www.java.com/en/\" target=\"_blank\">Java</a>"))
        assert(content.contains("<a href=\"http://php.net/\" target=\"_blank\">PHP</a>"))
      }

      // Verify
      verify(techService, times(2)).allRatings()
      verify(techService).votesFor(eminentUser.id)
      verify(techService).allCategories()
    }
  }

  behavior of "add"

  it should "not authorize anonymous user" in new TestScope {
    assertAnonymousUser(authTechController.add())
  }

  it should "handle submitted form" in new TestScope {
    withEminentUser(mainModel = false) {
      // Prepare
      val tech = Tech(
        id        = Identifiable.empty,
        handle    = HandleFactory.fromHumanName("IIS"),
        category  = TechCategory("server"),
        name      = "IIS",
        website   = new URI("http://www.iis.com/")
      )
      when(techService.upsert(tech))
        .thenReturn(Future.successful(tech.handle))

      // Execute
      val form: Map[String, String] = Map(
        "categoryHandle" -> tech.category.handle.value,
        "name" -> tech.name,
        "website" -> tech.website.toString
      )
      assertAuthResult(eminentUser, authTechController, authTechController.add(), form) { result =>
        assert(status(result) == SEE_OTHER)
        assert(redirectLocation(result).contains("/technologies"))
      }

      // Verify
      verify(techService).upsert(tech)
    }
  }

  behavior of "remove"

  it should "not authorize anonymous user" in new TestScope {
    assertAnonymousUser(authTechController.remove(TechRatingSpec.scalaRating.tech.handle.value))
  }

  it should "remove a technology and redirect" in new TestScope {
    withEminentUser(mainModel = false) {
      // Prepare
      when(techService.remove(TechRatingSpec.scalaRating.tech.handle))
        .thenReturn(Future.successful(TechRatingSpec.scalaRating.tech.handle))

      // Execute
      assertAuthResult(eminentUser, authTechController, authTechController.remove(TechRatingSpec.scalaRating.tech.handle.value)) { result =>
        assert(status(result) == SEE_OTHER)
        assert(redirectLocation(result).contains("/technologies"))
      }

      // Verify
      verify(techService).remove(TechRatingSpec.scalaRating.tech.handle)
    }
  }

  behavior of "voteUp"

  it should "not authorize anonymous user" in new TestScope {
    assertAnonymousUser(authTechController.voteUp(TechRatingSpec.scalaRating.tech.id.value))
  }

  it should "vote up a technology" in new TestScope {
    withEminentUser(mainModel = false) {
      // Prepare
      when(techService.voteUp(TechRatingSpec.scalaRating.tech.handle, eminentUser.id))
        .thenReturn(Future.successful(()))

      // Execute
      assertAuthResult(eminentUser, authTechController, authTechController.voteUp(TechRatingSpec.scalaRating.tech.handle.value)) { result =>
        assert(status(result) == SEE_OTHER)
        assert(redirectLocation(result).contains("/technologies"))
      }

      // Verify
      verify(techService).voteUp(TechRatingSpec.scalaRating.tech.handle, eminentUser.id)
    }
  }

  behavior of "voteDown"

  it should "not authorize anonymous user" in new TestScope {
    assertAnonymousUser(authTechController.voteDown(TechRatingSpec.phpRating.tech.id.value))
  }

  it should "vote down a technology" in new TestScope {
    withEminentUser(mainModel = false) {
      // Prepare
      when(techService.voteDown(TechRatingSpec.phpRating.tech.handle, eminentUser.id))
        .thenReturn(Future.successful(()))

      // Execute
      assertAuthResult(eminentUser, authTechController, authTechController.voteDown(TechRatingSpec.phpRating.tech.handle.value)) { result =>
        assert(status(result) == SEE_OTHER)
        assert(redirectLocation(result).contains("/technologies"))
      }

      // Verify
      verify(techService).voteDown(TechRatingSpec.phpRating.tech.handle, eminentUser.id)
    }
  }

  private class TestScope extends BaseAuthTestScope(application) {
    val authTechController = new AuthTechControllerImpl(
      authService,
      locationService,
      techService,
      application,
      messagesApi
    )
  }
}
