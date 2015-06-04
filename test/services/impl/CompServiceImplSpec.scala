package services.impl

import models.db
import models.domain.{Handle, Comp, CompSpec, TechSpec}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.FlatSpec
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatest.time.{Millis, Seconds, Span}
import reactivemongo.bson.BSONObjectID
import repositories.CompRepository
import services.{LocationService, TechService}

import scala.concurrent.Future

class CompServiceImplSpec extends FlatSpec with MockitoSugar with ScalaFutures {
  import models.domain.CompSpec._

  implicit override val patienceConfig = PatienceConfig(timeout = Span(2, Seconds), interval = Span(5, Millis))

  behavior of "upsert"

  it should "map user to DB and invoke repository" in new TestScope {
    // Prepare
    val userId = BSONObjectID.generate.stringify
    val compId = BSONObjectID.generate
    when(compRepository.upsert(any[db.Comp])).thenReturn(Future.successful(compId))

    // Execute
    assert(compService.upsert(
      comp        = avitech,
      techHandles = avitech.techRatings.map(_.tech.handle),
      userId      = db.CompSpec.avitech.authorId.stringify
    ).futureValue == compId.stringify)

    // Verify
    val compCaptor = ArgumentCaptor.forClass(classOf[db.Comp])
    verify(compRepository).upsert(compCaptor.capture())
    verifyNoMore()
    val comp = compCaptor.getValue

    // Assert
    assert(comp == db.CompSpec.avitech)
  }

  behavior of "all"

  it should "return all companies if no city or tech is provided" in new TestScope {
    // Prepare
    when(compRepository.all(None, None)).thenReturn(Future.successful(Seq(db.CompSpec.avitech, db.CompSpec.borci)))
    when(techService.allRatings()).thenReturn(Future.successful(TechSpec.allRatings))
    when(locationService.get(Handle(db.CompSpec.avitech.city))).thenReturn(Future.successful(CompSpec.avitech.city))
    when(locationService.get(Handle(db.CompSpec.borci.city))).thenReturn(Future.successful(CompSpec.borci.city))

    // Execute
    val result = compService.all(None, None).futureValue.toSet
    assertComp(avitech, result.find(_.id == avitech.id).get)
    assertComp(borci, result.find(_.id == borci.id).get)

    // Verify
    verify(compRepository).all(None, None)
    verify(techService).allRatings()
    verify(locationService).get(Handle(db.CompSpec.avitech.city))
    verify(locationService).get(Handle(db.CompSpec.borci.city))
    verifyNoMore()
  }

  private def assertComp(expected: Comp, actual: Comp): Unit = {
    // Partial assertions
    assertUnapplied(
      Comp.unapply(expected).get.productIterator.toSeq,
      Comp.unapply(actual).get.productIterator.toSeq)

    // Double-check
    assertResult(expected)(actual)
  }

  private def assertUnapplied(expected: Seq[Any], actual: Seq[Any]): Unit =
    expected.zip(actual).foreach { pair =>
      assertResult(pair._1)(pair._2)
  }

  private class TestScope {
    val compRepository = mock[CompRepository]
    val techService = mock[TechService]
    val locationService = mock[LocationService]
    val compService = new CompServiceImpl(compRepository, techService, locationService)

    def verifyNoMore(): Unit = {
      verifyNoMoreInteractions(compRepository)
      verifyNoMoreInteractions(techService)
      verifyNoMoreInteractions(locationService)
    }
  }
}
