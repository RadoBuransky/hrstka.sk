package sk.hrstka.models.domain

/**
 * Company search.
 *
 * @param terms Ordered sequence of search terms.
 */
case class CompSearchQuery(terms: Set[CompSearchTerm])

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