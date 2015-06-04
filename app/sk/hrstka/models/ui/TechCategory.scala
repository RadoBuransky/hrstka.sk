package sk.hrstka.models.ui

import sk.hrstka.models

/**
 * Technology category.
 *
 * @param handle Human-friendly identifier.
 * @param en English name.
 */
case class TechCategory(handle: String,
                        en: String)

object TechCategoryFactory {
  def apply(techCategory: models.domain.TechCategory): TechCategory =
    TechCategory(
      handle  = techCategory.handle.value,
      en      = techCategory.en
    )
}