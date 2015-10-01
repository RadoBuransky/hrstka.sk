package sk.hrstka.controllers.impl

import com.google.inject.{Inject, Singleton}
import jp.t2v.lab.play2.auth.OptionalAuthElement
import jp.t2v.lab.play2.stackc.RequestWithAttributes
import play.api.Application
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, Result}
import sk.hrstka.controllers.CompController
import sk.hrstka.controllers.auth.impl.HrstkaAuthConfig
import sk.hrstka.models.domain._
import sk.hrstka.models.ui.{CompFactory, CompRatingFactory, Html}
import sk.hrstka.models.{domain, ui}
import sk.hrstka.services._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
final class CompControllerImpl @Inject() (compService: CompService,
                                          compSearchService: CompSearchService,
                                          markdownService: MarkdownService,
                                          protected val authService: AuthService,
                                          protected val techService: TechService,
                                          protected val locationService: LocationService,
                                          protected val application: Application,
                                          val messagesApi: MessagesApi)
  extends BaseController with CompController with MainModelProvider with HrstkaAuthConfig with OptionalAuthElement {

  override def get(businessNumber: String): Action[AnyContent] = AsyncStack { implicit request =>
    for {
      comp <- compService.get(BusinessNumber(businessNumber))
      uiComp = compToUi(comp)
      vote <- transform(loggedIn.map(userId => compService.voteFor(comp.businessNumber, userId.id)))
      result <- withMainModel(loggedIn, title = uiComp.title, description = uiComp.description ) { implicit mainModel =>
        Ok(sk.hrstka.views.html.comp(uiComp, vote.flatten.map(_.value)))
      }
    } yield result
  }

  override def women = AsyncStack { implicit request =>
    for {
      topWomen <- compService.topWomen()
      result <- withMainModel(loggedIn, title = "Women Who Code", description = "Companies with many women programmers.") { implicit mainModel =>
        Ok(sk.hrstka.views.html.women(topWomen.map(compRatingToUi)))
      }
    } yield result
  }

  override def search = AsyncStack { implicit request =>
    // Get query parameter
    val query = request.getQueryString("q") match {
      case Some(q) => q
      case None => ""
    }

    // Convert it to query model
    compSearchService.compSearchQuery(query).flatMap { compSearchQuery =>
      if (compSearchQuery.cityTerms.size <= 1 &&
          compSearchQuery.techTerms.size <= 1 &&
          compSearchQuery.cityTerms.size + compSearchQuery.techTerms.size > 0 &&
          compSearchQuery.fulltextTerms.isEmpty) {

        val cityHandle = compSearchQuery.cityTerms.headOption.map(_.cityHandle.value).getOrElse(CompControllerImpl.anywhere)
        val techHandle = compSearchQuery.techTerms.headOption.map(_.techHandle.value).getOrElse("")

        Future.successful(Redirect(sk.hrstka.controllers.routes.CompController.cityTech(cityHandle, techHandle)))
      }
      else
        search(compSearchQuery)
    }
  }

  override def cityTech(cityHandle: String, techHandle: String) = AsyncStack { implicit request =>
    // Preprocess city handle
    val trimmedCityHandle = cityHandle.trim.toLowerCase
    val citySearchTerm = if (trimmedCityHandle.isEmpty || trimmedCityHandle == CompControllerImpl.anywhere)
      None
    else
      Some(CitySearchTerm(Handle(trimmedCityHandle)))

    // Preprocess tech handle
    val trimmedTechHandle = techHandle.trim.toLowerCase
    val techSearchTerm = if (trimmedTechHandle.isEmpty)
      None
    else
      Some(TechSearchTerm(Handle(techHandle)))

    // Search
    search(CompSearchQuery(Set(citySearchTerm, techSearchTerm).flatten))
  }

  private def search[A](compSearchQuery: CompSearchQuery)(implicit request: RequestWithAttributes[A]): Future[Result] = {
    val cityHandle = if (compSearchQuery.cityTerms.size == 1) Some(compSearchQuery.cityTerms.head.cityHandle) else None
    val techHandle = if (compSearchQuery.techTerms.size == 1) Some(compSearchQuery.techTerms.head.techHandle) else None

    for {
      compRatings <- compService.search(compSearchQuery)
      city <- cityForHandle(cityHandle)
      tech <- techForHandle(techHandle)
      headlineText = headline(city, tech)
      result <- withMainModel(loggedIn,
        title = headlineText,
        description = cityTechDescription(headlineText, compRatings),
        searchQuery = compSearchQuery.raw) { implicit mainModel =>
        Ok(sk.hrstka.views.html.index(headlineText, compRatings.map(compRating => compRatingToUi(compRating))))
      }
    } yield result
  }

  private def transform[A](o: Option[Future[A]]): Future[Option[A]] =
    o.map(f => f.map(Option(_))).getOrElse(Future.successful(None))

  private def cityTechDescription(headlineText: String, compRatings: Seq[CompRating]): String =
    headlineText + ". " + compRatings.take(10).map(_.comp.name).mkString(", ") + "."

  private def compRatingToUi(compRating: domain.CompRating): ui.CompRating =
    CompRatingFactory(compToUi(compRating.comp), compRating.value)

  private def compToUi(comp: domain.Comp): ui.Comp = {
    CompFactory(comp, Html(markdownService.toHtml(preprocessMarkdown(comp.markdownNote))))
  }

  private def preprocessMarkdown(markdownNote: String): String = {
    // Find all headings and increase their level +3
    val processedLines = markdownNote.lines.map { line =>
      if (line.startsWith("#"))
        "##" + line
      else
        line
    }

    processedLines.mkString(System.lineSeparator)
  }

  private def headline(city: Option[City], tech: Option[Tech]): String = {
    if (city.isEmpty && tech.isEmpty)
      "All tech companies"
    else {
      val techHeadline = tech.map(_.name).getOrElse("")
      val cityHeadline = city.map(c => " in " + c.name + " city").getOrElse("")

      "Tech companies" + (
        if (tech.isEmpty)
          cityHeadline
        else
          " that use " + (
            if (city.isEmpty)
              techHeadline
            else
              techHeadline + cityHeadline
            )
        )
    }
  }

  private def techForHandle(techHandle: Option[Handle]): Future[Option[Tech]] = techHandle match {
    case Some(handle) => techService.getByHandle(handle).map(Some(_))
    case None => Future.successful(None)
  }

  private def cityForHandle(cityHandle: Option[Handle]): Future[Option[City]] = cityHandle match {
    case Some(handle) => locationService.city(handle).map(Some(_))
    case None => Future.successful(None)
  }
}

object CompControllerImpl {
  val anywhere = "anywhere"
}