package models.domain

sealed trait CompQuery {
  def keywords: Seq[String]
}

private class CompQueryImpl(val keywords: Seq[String]) extends CompQuery

object CompQuery {
  def apply(queryString: String): CompQuery = {
    new CompQueryImpl(
      queryString
          .toLowerCase
          .split(Array(',', ' ', ';'))
          .filter(_.length > 0)
          .distinct
          .map(_.replace('-',' '))
          .map(_.replace("\"", ""))
          .map(_.replace("'", "")))
  }
}
