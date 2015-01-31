package controllers

import common.SupportedLang
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index(SupportedLang.defaultLang))
  }

}