package models.ui

import models.domain

/**
 * Technology category.
 *
 * @param handle Human-friendly identifier.
 * @param en English name.
 */
case class TechCategory(handle: String,
                        en: String)

object TechCategory {
  def apply(techCategory: domain.TechCategory): TechCategory =
    TechCategory(
      handle  = techCategory.handle.value,
      en      = techCategory.en
    )
}