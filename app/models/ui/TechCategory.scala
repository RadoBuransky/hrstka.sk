package models.ui

import models.domain

/**
 * Technology category.
 *
 * @param handle Human-friendly identifier.
 * @param sk Slovak name.
 */
case class TechCategory(handle: String,
                        sk: String)

object TechCategory {
  def apply(techCategory: domain.TechCategory): TechCategory =
    TechCategory(
      handle  = techCategory.handle.value,
      sk      = techCategory.sk
    )
}