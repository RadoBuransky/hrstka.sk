package sk.hrstka.models.domain

sealed trait BusinessNumber {
  def value: String

  if (value.trim.isEmpty)
    throw new IllegalArgumentException(s"Business number cannot be empty! [$value]")
}

object BusinessNumber {
  def apply(value: String): BusinessNumber =
    // Only Slovak ICO is supported
    SlovakBusinessNumber(value)
}

case class SlovakBusinessNumber(value: String) extends BusinessNumber {
  if (value.length != 6 && value.length != 8)
    throw new IllegalArgumentException(s"Length must be 6 or 8! [$value]")

  if (SlovakBusinessNumberDefinition.pattern.findFirstIn(value).isEmpty)
    throw new IllegalArgumentException(s"Only numbers are allowed! [$value]")

  if (value.length == 8 && value.toLong % 11 != 0)
    throw new IllegalArgumentException(s"Not dividable by 11! [$value]")
}

private object SlovakBusinessNumberDefinition {
  val pattern = "^[0-9]*$".r
}
