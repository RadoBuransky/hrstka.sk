package sk.hrstka.views.helpers

object Formatting {
  def govBizToString(govBiz: Option[BigDecimal]): String = govBiz.map("%1.0f%%".format(_)).getOrElse("")
}
