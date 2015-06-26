package sk.hrstka.models.ui

import sk.hrstka.common.HrstkaException

sealed trait FormattedText {
  def markdown: String
  def html: String
}

case class Markdown(markdown: String) extends FormattedText {
  override def html = throw new HrstkaException("This is Markdown text, not HTML!")
}

case class Html(html: String) extends FormattedText {
  override def markdown = throw new HrstkaException("This is HTML text, not Markdown!")
}
