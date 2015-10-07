package sk.hrstka.services.impl.scraping

case class ScrapedCompStatic(name: String)

/**
 * Generic HTML company info scraper.
 */
class GenericCompScraper {
  def scrape(html: String): ScrapedCompStatic = {
    ScrapedCompStatic("aaa")
  }
}

object GenericCompScraper {
  def apply(html: String): ScrapedCompStatic = new GenericCompScraper().scrape(html)
}