package sk.hrstka.controllers.impl

import com.google.inject.{Inject, Singleton}
import jp.t2v.lab.play2.auth.OptionalAuthElement
import play.api.{Logger, Application}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent}
import sk.hrstka.controllers.CompController
import sk.hrstka.controllers.auth.impl.HrstkaAuthConfig
import sk.hrstka.models.domain._
import sk.hrstka.models.ui.{CompFactory, CompRatingFactory, Html, Tag}
import sk.hrstka.models.{domain, ui}
import sk.hrstka.services._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
final class CompControllerImpl @Inject() (compService: CompService,
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
      result <- withMainModel(None, None, loggedIn, title = uiComp.title, description = uiComp.description ) { implicit mainModel =>
        Ok(sk.hrstka.views.html.comp(uiComp, vote.flatten.map(_.value)))
      }
    } yield result
  }

  override def women = AsyncStack { implicit request =>
    for {
      topWomen <- compService.topWomen()
      result <- withMainModel(None, None, loggedIn, title = "Women Who Code", description = "Companies with many women programmers.") { implicit mainModel =>
        Ok(sk.hrstka.views.html.women(topWomen.map(compRatingToUi)))
      }
    } yield result
  }

  override def search = AsyncStack { implicit request =>
    val query = request.getQueryString("q") match {
      case Some(q) => q
      case None => ""
    }

    for {
      compRatings <- compService.search(query)
      result <- withMainModel(None, None, loggedIn, title = "") { implicit mainModel =>
        Ok(sk.hrstka.views.html.index("", compRatings.map(compRating => compRatingToUi(compRating))))
      }
    } yield result
  }
  override def cityTech(cityHandle: String, techHandle: String) =
    cityTechAction(Option(cityHandle).filter(_.trim.nonEmpty), Option(techHandle).filter(_.trim.nonEmpty))

  private def transform[A](o: Option[Future[A]]): Future[Option[A]] =
    o.map(f => f.map(Option(_))).getOrElse(Future.successful(None))

  private def cityTechDescription(headlineText: String, compRatings: Seq[CompRating]): String =
    "Top technology companies that use " + headlineText + ". " + compRatings.take(10).map(_.comp.name).mkString(", ") + "."

  private def cityTechAction(cityHandle: Option[String], techHandle: Option[String]) = AsyncStack { implicit request =>
      for {
        city <- cityForHandle(cityHandle)
        tech <- techForHandle(techHandle)
        compRatings <- compService.all(city.map(_.handle), tech.map(_.handle))
        headlineText = headline(city, tech)
        result <- withMainModel(cityHandle, techHandle, loggedIn,
          title = headlineText + " - Hŕstka", description = cityTechDescription(headlineText, compRatings)) { implicit mainModel =>
          Ok(sk.hrstka.views.html.index(
            headlineText,
            compRatings.map(compRating => compRatingToUi(compRating))))
        }
      } yield result
    }

  private def compRatingToUi(compRating: domain.CompRating): ui.CompRating =
    CompRatingFactory(compToUi(compRating.comp), compRating.value)

  private def compToUi(comp: domain.Comp): ui.Comp = {
    CompFactory(comp, Html(markdownService.toHtml(preprocessMarkdown(comp.markdownNote))), Tag(comp.techRatings))
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
    val cityHeadline = city
      .map(c => " in " + c.name + " city")
      .getOrElse("")
    val techHeadline = tech.map(_.name).getOrElse("")
    if (city.isEmpty && tech.isEmpty)
      "All technology companies"
    else
      if (tech.isEmpty)
        "Companies" + cityHeadline
      else
        techHeadline + cityHeadline
  }

  private def techForHandle(techHandle: Option[String]): Future[Option[Tech]] = techHandle match {
    case Some(handle) => techService.getByHandle(Handle(handle)).map(Some(_))
    case None => Future.successful(None)
  }


  private def cityForHandle(cityHandle: Option[String]): Future[Option[City]] = cityHandle match {
    case Some(handle) => locationService.city(Handle(handle)).map(Some(_))
    case None => Future.successful(None)
  }
}
