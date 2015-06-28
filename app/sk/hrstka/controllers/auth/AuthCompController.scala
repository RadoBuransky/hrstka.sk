package sk.hrstka.controllers.auth

import com.google.inject._
import play.api.mvc._
import sk.hrstka.controllers.auth.impl.AuthCompControllerImpl

case class AddCompForm(name: String,
                       website: String,
                       city: String,
                       businessNumber: String,
                       employeeCount:Option[Int],
                       codersCount: Option[Int],
                       femaleCodersCount: Option[Int],
                       govBiz: Option[BigDecimal],
                       note: String,
                       products: Boolean,
                       services: Boolean,
                       internal: Boolean,
                       techs: List[String],
                       joel: List[Int])

/**
 * Authorized company controller.
 */
@ImplementedBy(classOf[AuthCompControllerImpl])
trait AuthCompController {
  /**
   * Gets HTML view with a form to add a company.
   *
   * @return HTML view.
   */
  def addForm(): Action[AnyContent]

  /**
   * Gets HTML view with a form to edit a company.
   *
   * @param businessNumber Company business number.
   * @return HTML view.
   */
  def editForm(businessNumber: String): Action[AnyContent]

  /**
   * Handles sumbitted form with a company to add or update.
   *
   * @param compId Company identifier.
   * @return Redirects to the index page.
   */
  def save(compId: Option[String]): Action[AnyContent]

  /**
   * Handles vote up.
   *
   * @param businessNumber Company business number.
   * @return Redirect to the company page.
   */
  def voteUp(businessNumber: String): Action[AnyContent]

  /**
   * Handles vote down.
   *
   * @param businessNumber Company business number.
   * @return Redirect to the company page.
   */
  def voteDown(businessNumber: String): Action[AnyContent]
}

