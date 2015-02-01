package controllers

import play.api.mvc.Controller
import models.domain

abstract class BaseController extends Controller {
  protected def userId: domain.Identifiable.Id = "54ce855363ecfca285f788c8"
}
