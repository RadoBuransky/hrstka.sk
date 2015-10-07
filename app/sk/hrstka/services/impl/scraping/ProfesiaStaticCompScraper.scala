package sk.hrstka.services.impl.scraping

class ProfesiaStaticCompScraper extends BaseStaticCompScraper {
  override protected val nameSelector = "h2"
  override protected val websiteSelector = "table.offer-details a[target=new][rel=nofollow]"
}

object ProfesiaStaticCompScraper {
  def apply(html: String): StaticCompScraperResult = new ProfesiaStaticCompScraper().scrape(html)
}
