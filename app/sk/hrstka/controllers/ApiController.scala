package sk.hrstka.controllers

import com.google.inject._
import play.api.mvc._
import sk.hrstka.controllers.impl.ApiControllerImpl

/**
 * API controller.
 */
@ImplementedBy(classOf[ApiControllerImpl])
trait ApiController {
  /**
   * Gets JSON array with all companies.
   *
   * @return JSON array.
   */
  def comps(): Action[AnyContent]

  /**
   * Gets JSON array with all technologies.
   *
   * @return JSON array.
   */
  def techs(): Action[AnyContent]

  /**
   * Gets JSON array with all cities.
   *
   * @return JSON array.
   */
  def cities(): Action[AnyContent]
}