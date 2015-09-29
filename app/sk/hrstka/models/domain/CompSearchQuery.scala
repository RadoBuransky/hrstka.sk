package sk.hrstka.models.domain

/**
 * Company search.
 *
 * @param terms Ordered sequence of search terms.
 */
case class CompSearchQuery(terms: Set[CompSearchTerm]) {
  lazy val cityTerms: Set[CitySearchTerm] = terms.collect { case cityTerm: CitySearchTerm => cityTerm }
  lazy val techTerms: Set[TechSearchTerm] = terms.collect { case techTerm: TechSearchTerm => techTerm }
  lazy val fulltextTerms: Set[FulltextSearchTerm] = terms.collect { case fulltextTerm: FulltextSearchTerm => fulltextTerm }

  def raw = (cityTerms.map(_.cityHandle.value) ++ techTerms.map(_.techHandle.value) ++ fulltextTerms.map(_.text)).mkString(" ")
}

/**
 * Company search term marker trait.
 */
sealed trait CompSearchTerm

/**
 * Search by a technology.
 *
 * @param techHandle Technology handle.
 */
case class TechSearchTerm(techHandle: Handle) extends CompSearchTerm

/**
 * Search by a city.
 *
 * @param cityHandle City handle.
 */
case class CitySearchTerm(cityHandle: Handle) extends CompSearchTerm

/**
 * Fulltext search.
 *
 * @param text Text to use in full-text searching.
 */
case class FulltextSearchTerm(text: String) extends CompSearchTerm

/**
 * Company search rank.
 */
sealed trait CompSearchRank

/**
 * Some match. The higher value, the better. No limits.
 */
case class MatchedRank(value: BigDecimal) extends CompSearchRank {
  if (value <= 0)
    throw new IllegalArgumentException(s"Rank value must be positive! [$value]")
}

/**
 * No match.
 */
case object NoMatchRank extends CompSearchRank