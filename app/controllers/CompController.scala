package controllers

import java.net.URL

import common.SupportedLang
import models.domain.{Handle, City, CompQuery}
import models.{domain, ui}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, AnyContent, Call, Result}
import services.{CompService, TechService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class AddCompForm(name: String,
                       website: String,
                       city: String,
                       employeeCount:Option[Int],
                       codersCount: Option[Int],
                       femaleCodersCount: Option[Int],
                       note: String,
                       products: Boolean,
                       services: Boolean,
                       internal: Boolean,
                       techs: List[String],
                       joel: List[Int])
case class AddTechToCompForm(techName: String)

trait CompController {
  def addForm: Action[AnyContent]
  def editForm(compId: String): Action[AnyContent]
  def save(compId: Option[String]): Action[AnyContent]
  def all: Action[AnyContent]
  def locationTech(location: String, tech: String): Action[AnyContent]
}

object CompController {
  val addCompForm = Form(
    mapping(
      "compName" -> text,
      "website" -> text,
      "city" -> text,
      "employeeCount" -> optional(number),
      "codersCount" -> optional(number),
      "femaleCodersCount" -> optional(number),
      "note" -> text,
      "products" -> boolean,
      "services" -> boolean,
      "internal" -> boolean,
      "techs" -> list(text),
      "joel" -> list(number)
    )(AddCompForm.apply)(AddCompForm.unapply)
  )

  val addTechToCompForm = Form(
    mapping(
      "techName" -> text
    )(AddTechToCompForm.apply)(AddTechToCompForm.unapply)
  )

  val joelQuestions = List(
    "Do you use source control?",
    "Can you make a build in one step?",
    "Do you make daily builds?",
    "Do you have a bug database?",
    "Do you fix bugs before writing new code?",
    "Do you have an up-to-date schedule?",
    "Do you have a spec?",
    "Do programmers have quiet working conditions?",
    "Do you use the best tools money can buy?",
    "Do you have testers?",
    "Do new candidates write code during their interview?",
    "Do you do hallway usability testing?"
  )

  def apply(compService: CompService,techService: TechService): CompController =
    new CompControllerImpl(compService, techService)
}

private class CompControllerImpl(compService: CompService,
                                 techService: TechService) extends BaseController with CompController {
  import controllers.CompController._

  override def addForm: Action[AnyContent] = Action.async {
    edit(None, AppLoader.routes.compController.save(None))
  }

  override def editForm(compId: String): Action[AnyContent] = Action.async {
    compService.get(compId).flatMap { comp =>
      edit(Some(comp), AppLoader.routes.compController.save(Some(compId)))
    }
  }

  private def edit(comp: Option[domain.Comp], action: Call): Future[Result] =
    techService.all().map { techs =>
      val ts = techs.map(t => (t.name, comp.exists(_.techs.exists(_.name == t.name))))
      Ok(views.html.compEdit(SupportedLang.defaultLang, comp.map(ui.Comp.apply), ts, joelQuestions, action))
    }

  override def save(compId: Option[String]) = withForm(addCompForm) { form =>
    val result = if (compId.isEmpty) {
      compService.insert(form.name, new URL(form.website), form.city, form.employeeCount, form.codersCount, form.femaleCodersCount,
        form.note, userId, form.products, form.services, form.internal, form.techs, form.joel.toSet)
    }
    else {
      compService.update(domain.Comp(
        id = compId.get,
        name = form.name,
        website = new URL(form.website),
        city = City(Handle.fromHumanName(form.city), form.city),
        employeeCount = form.employeeCount,
        codersCount = form.codersCount,
        femaleCodersCount = form.femaleCodersCount,
        note = form.note,
        products = form.products,
        services = form.services,
        internal = form.internal,
        techs = Nil,
        joel = form.joel.toSet
      ),
      form.techs,
      userId)
    }

    result.map { Unit =>
      Redirect(AppLoader.routes.compController.all())
    }
  }

  override def all = Action.async { implicit request =>
    compService.all().map { comps =>
      val query = request.queryString.get("q").map(q => CompQuery(q.mkString(",")))

      // TODO: Redirect to locationTech if query contains a location (and a tech)?
      // TODO: List 5 cities with the most companies
      // TODO: If a city is specified, list top 5 technologies by rating / count

      Ok(views.html.comps(
        SupportedLang.defaultLang,
        comps.map(ui.Comp(_)),
        query.map(_.keywords.mkString(","))))
    }
  }

  def locationTech(location: String, tech: String): Action[AnyContent] = {
    all
  }
}