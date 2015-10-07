package sk.hrstka.services.impl.scraping

class ProfesiaStaticCompScraper extends BaseStaticCompScraper {
  protected val compNameSelector = "h2"
}

object ProfesiaStaticCompScraper {
  def apply(html: String): StaticCompScraperResult = new ProfesiaStaticCompScraper().scrape(html)
}
