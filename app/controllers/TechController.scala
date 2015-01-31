package controllers

import common.SupportedLang
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc._
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.concurrent.Execution.Implicits.defaultContext

case class AddTechForm(techName: String)

trait TechController {
  def add: Action[AnyContent]
  def all: Action[AnyContent]
}

object TechController {
  val addTechForm = Form(
    mapping(
      "techName" -> text
    )(AddTechForm.apply)(AddTechForm.unapply)
  )

  def apply(): TechController = new TechControllerImpl
}

private class TechControllerImpl extends Controller with TechController with MongoController {
  import TechController._

  def collection: JSONCollection = db.collection[JSONCollection]("addTechEvents")

  override def add: Action[AnyContent] = Action { implicit request =>
    val addTech = addTechForm.bindFromRequest.get

    val json = Json.obj(
      "name" -> addTech.techName,
      "created" -> new java.util.Date().getTime)

    collection.insert(json).map(lastError =>
      Ok("Mongo LastError: %s".format(lastError)))

    Ok(views.html.technologies(SupportedLang.defaultLang, addTech.techName + " added"))
  }

  override def all: Action[AnyContent] = Action {
    Ok(views.html.technologies(SupportedLang.defaultLang, ""))
  }
}