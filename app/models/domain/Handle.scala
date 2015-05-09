package models.domain

import java.text.Normalizer

case class Handle(value: String)

object Handle {
  def fromHumanName(humanName: String): Handle = {
    Handle(removeDiacritics(humanName).replace(' ', '-').toLowerCase)
  }

  private def removeDiacritics(text: String): String = {
    Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
  }
}
