package models.domain

import java.text.Normalizer

case class Handle(value: String)

object Handle {
  def fromHumanName(humanName: String) = Handle(removeDiacritics(humanName).trim.replace(' ', '-').toLowerCase)
  private def removeDiacritics(text: String) =
    Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
}
