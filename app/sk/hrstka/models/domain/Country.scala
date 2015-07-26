package sk.hrstka.models.domain

/**
 * Country.
 */
sealed trait Country {
  /**
   * ISO 3166 alpha-2 country code (top-level domain).
   */
  def code: Iso3166

  /**
   * English name of the country.
   */
  def en: String
}

/**
 * ISO 3166 alpha-2 country code (top-level domain).
 */
case class Iso3166(value: String) {
  if (!value.matches("[A-Z][A-Z]"))
    throw new IllegalArgumentException(s"Not a valid ISO 3166 country code! [$value]")
}

case object Slovakia extends Country {
  val code = Iso3166("SK")
  val en = "Slovakia"
}

case object CzechRepublic extends Country {
  val code = Iso3166("CZ")
  val en = "Czech Republic"
}

case object Austria extends Country {
  val code = Iso3166("AT")
  val en = "Austria"
}

case object Hungary extends Country {
  val code = Iso3166("HU")
  val en = "Hungary"
}

case object Poland extends Country {
  val code = Iso3166("PL")
  val en = "Poland"
}

case object Ukraine extends Country {
  val code = Iso3166("UA")
  val en = "Ukraine"
}

case object Germany extends Country {
  val code = Iso3166("DE")
  val en = "Germany"
}