package sk.hrstka.views.helpers

object Selected {
  def apply[T](item: T, entityOption: Option[T], compare: (T) => Any): String =
    entityOption match {
      case Some(entity) if compare(item) == compare(entity) => "selected"
      case _ => ""
    }
}
