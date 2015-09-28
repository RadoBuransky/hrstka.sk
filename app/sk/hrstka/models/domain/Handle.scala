package sk.hrstka.models.domain

import java.text.Normalizer

case class Handle(value: String)

object HandleFactory {
  def fromHumanName(humanName: String) = Handle(removeDiacritics(humanName).trim.replace(' ', '-').toLowerCase)
  def removeDiacritics(text: String) =
    Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
}
