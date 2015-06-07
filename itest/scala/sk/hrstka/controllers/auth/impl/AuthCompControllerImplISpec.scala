package sk.hrstka.controllers.auth.impl

import java.net.URL

import org.mockito.Mockito._
import org.scalatest.DoNotDiscover
import play.api.Application
import play.api.test.Helpers._
import reactivemongo.bson.BSONObjectID
import sk.hrstka.BaseStandaloneFakeApplicationSuites
import sk.hrstka.controllers.itest.BaseControllerISpec
import sk.hrstka.models.domain._
import sk.hrstka.services.CompService

import scala.concurrent.Future

@DoNotDiscover
class StandaloneAuthCompControllerImplISpec extends BaseStandaloneFakeApplicationSuites {
  override val nestedSuites = Vector(new AuthCompControllerImplISpec(app))
}

@DoNotDiscover
class AuthCompControllerImplISpec(application: Application) extends BaseControllerISpec {
  behavior of "addForm"

  it should "not authorize visitors" in new TestScope {
    assertAnonymousUser(authCompController.addForm())
  }

  it should "get HTML view with a form to add a company" in new TestScope {
    withAuthorisedUser {
      assertAuthView(authCompController, authCompController.addForm()) { content =>
        assert(content.contains("<form action=\"/programovanie/firma\" method=\"post\">"))
      }
    }
  }

  behavior of "editForm"

  it should "not authorize visitors" in new TestScope {
    assertAnonymousUser(authCompController.editForm(CompSpec.avitech.id.value))
  }

  it should "get HTML view with a form to edit a company" in new TestScope {
    withAuthorisedUser {
      // Prepare
      when(compService.get(CompSpec.avitech.id))
        .thenReturn(Future.successful(CompSpec.avitech))

      // Execute
      assertAuthView(authCompController, authCompController.editForm(CompSpec.avitech.id.value)) { content =>
        assert(content.contains("<form action=\"/programovanie/firma?compId="))
      }

      // Verify
      verify(compService).get(CompSpec.avitech.id)
    }
  }

  behavior of "save"

  it should "not authorize visitors to add a company" in new TestScope {
    assertAnonymousUser(authCompController.save(None))
  }

  it should "not authorize visitors to edit a company" in new TestScope {
    assertAnonymousUser(authCompController.save(Some(CompSpec.avitech.id.value)))
  }

  it should "handle sumbitted form with a company to add" in new TestScope {
    save(None)
  }

  it should "handle sumbitted form with a company to edit" in new TestScope {
    save(Some(BSONObjectID.generate.stringify))
  }

  private class TestScope extends BaseAuthTestScope(application) {
    val compService = mock[CompService]

    val authCompController = new AuthCompControllerImpl(
      compService,
      authService,
      techService,
      locationService,
      application,
      messagesApi
    )

    override def verifyNoMore(): Unit = {
      verifyNoMoreInteractions(compService)
      super.verifyNoMore()
    }

    def save(compId: Option[String]): Unit = {
      postWithAuthorisedUser {
        val comp = Comp(
          id = compId.map(Id).getOrElse(Identifiable.empty),
          name = "New comp",
          website = new URL("http://www.comp.top/"),
          city = CitySpec.noveZamky,
          employeeCount = None,
          codersCount = None,
          femaleCodersCount = None,
          note = "some note",
          products = true,
          services = true,
          internal = false,
          techRatings = Set.empty,
          joel = Set(3, 5, 11))

        // Prepare
        val techHandles = Set(
          TechRatingSpec.scalaRating.tech.handle,
          TechRatingSpec.javaRating.tech.handle,
          TechRatingSpec.akkaRating.tech.handle
        )
        val newCompId = compId.map(Id).getOrElse(Identifiable.fromBSON(BSONObjectID.generate))
        when(locationService.getOrCreateCity(CitySpec.noveZamky.sk))
          .thenReturn(Future.successful(CitySpec.noveZamky))
        when(compService.upsert(comp, techHandles, authUser.id))
          .thenReturn(Future.successful(newCompId))

        // Execute
        val form: Map[String, String] = Map(
          "compName" -> comp.name,
          "website" -> comp.website.toString,
          "city" -> comp.city.sk,
          "employeeCount" -> "",
          "codersCount" -> "",
          "femaleCodersCount" -> "",
          "note" -> comp.note,
          "products" -> "true",
          "services" -> "true",
          "internal" -> "false",
          "techs[0]" -> "scala",
          "techs[1]" -> "java",
          "techs[2]" -> "akka",
          "joel[0]" -> "3",
          "joel[1]" -> "5",
          "joel[2]" -> "11"
        )
        assertAuthResult(authCompController, authCompController.save(compId), form) { result =>
          assert(status(result) == SEE_OTHER)
        }

        // Verify
        verify(locationService).getOrCreateCity(CitySpec.noveZamky.sk)
        verify(compService).upsert(comp, techHandles, authUser.id)
      }
    }
  }
}
