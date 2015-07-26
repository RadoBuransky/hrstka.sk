package sk.hrstka.models.domain

import sk.hrstka.common.HrstkaException

object CountrySpec {
    val all = Seq(
      Slovakia,
      CzechRepublic,
      Austria,
      Hungary,
      Poland,
      Ukraine,
      Germany
    )

    def apply(countryCode: String): Country = all.find(_.code.value == countryCode) match {
      case Some(country) => country
      case None => throw new HrstkaException(s"No country for code! [$countryCode]")
    }
}
