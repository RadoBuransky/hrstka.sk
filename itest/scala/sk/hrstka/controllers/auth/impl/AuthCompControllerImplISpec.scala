package sk.hrstka.controllers.auth.impl

import java.net.URI

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
    withEminentUser() {
      // Prepare
      when(techService.allRatings())
        .thenReturn(Future.successful(TechRatingSpec.allRatings))

      assertAuthView(eminentUser, authCompController, authCompController.addForm()) { content =>
        assert(content.contains("<form action=\"/programming/company\" method=\"post\">"))
      }

      // Verify
      verify(techService).allRatings()
    }
  }

  behavior of "editForm"

  it should "not authorize visitors" in new TestScope {
    assertAnonymousUser(authCompController.editForm(CompSpec.avitech.id.value))
  }

  it should "get HTML view with a form to edit a company" in new TestScope {
    withEminentUser() {
      // Prepare
      when(compService.get(CompSpec.avitech.businessNumber))
        .thenReturn(Future.successful(CompSpec.avitech))
      when(techService.allRatings())
        .thenReturn(Future.successful(TechRatingSpec.allRatings))

      // Execute
      assertAuthView(eminentUser, authCompController, authCompController.editForm(CompSpec.avitech.businessNumber.value)) { content =>
        assert(content.contains("<form action=\"/programming/company?compId="))
      }

      // Verify
      verify(techService).allRatings()
      verify(compService).get(CompSpec.avitech.businessNumber)
    }
  }

  behavior of "save"

  it should "not authorize visitors to add a company" in new TestScope {
    assertAnonymousUser(authCompController.save(None))
  }

  it should "not authorize visitors to edit a company" in new TestScope {
    assertAnonymousUser(authCompController.save(Some(CompSpec.avitech.id.value)))
  }

  it should "handle sumbitted form with a company to add" in new SaveTestScope {
    save(None)
  }

  it should "handle sumbitted form with a company to edit" in new SaveTestScope {
    save(Some(BSONObjectID.generate.stringify))
  }

  private class SaveTestScope extends TestScope {
    def save(compId: Option[String]): Unit = {
      withEminentUser(mainModel = false) {
        val comp = Comp(
          id = compId.map(Id).getOrElse(Identifiable.empty),
          name = "New comp",
          website = new URI("http://www.comp.top/"),
          city = CitySpec.noveZamky,
          businessNumber = BusinessNumber("35887401"),
          employeeCount = None,
          codersCount = None,
          femaleCodersCount = None,
          markdownNote = "some note",
          products = true,
          services = true,
          internal = false,
          techRatings = Seq.empty,
          joel = Set(3, 5, 11),
          govBiz = Some(42))

        // Prepare
        val techHandles = Set(
          TechRatingSpec.scalaRating.tech.handle,
          TechRatingSpec.javaRating.tech.handle,
          TechRatingSpec.akkaRating.tech.handle
        )
        val newCompId = compId.map(Id).getOrElse(Identifiable.fromBSON(BSONObjectID.generate))
        when(locationService.getOrCreateCity(CitySpec.noveZamky.en))
          .thenReturn(Future.successful(CitySpec.noveZamky))
        when(compService.upsert(comp, techHandles, eminentUser.id))
          .thenReturn(Future.successful(comp.businessNumber))

        // Execute
        val form: Map[String, String] = Map(
          "compName" -> comp.name,
          "website" -> comp.website.toString,
          "city" -> comp.city.en,
          "businessNumber" -> comp.businessNumber.value,
          "employeeCount" -> "",
          "codersCount" -> "",
          "femaleCodersCount" -> "",
          "note" -> comp.markdownNote,
          "products" -> "true",
          "services" -> "true",
          "internal" -> "false",
          "techs[0]" -> "scala",
          "techs[1]" -> "java",
          "techs[2]" -> "akka",
          "joel[0]" -> "3",
          "joel[1]" -> "5",
          "joel[2]" -> "11",
          "govBiz" -> comp.govBiz.get.toString
        )
        assertAuthResult(eminentUser, authCompController, authCompController.save(compId), form) { result =>
          assert(status(result) == SEE_OTHER)
        }

        // Verify
        verify(locationService).getOrCreateCity(CitySpec.noveZamky.en)
        verify(compService).upsert(comp, techHandles, eminentUser.id)
      }
    }
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
  }
}
