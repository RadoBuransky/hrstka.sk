package sk.hrstka.models.domain

/**
 * Company search.
 *
 * @param terms Ordered sequence of search terms.
 */
case class CompSearch(terms: Set[CompSearchTerm])

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