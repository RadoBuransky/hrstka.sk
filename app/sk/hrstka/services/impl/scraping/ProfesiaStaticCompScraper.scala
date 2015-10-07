package sk.hrstka.services.impl.scraping

class ProfesiaStaticCompScraper extends BaseStaticCompScraper {
  override protected val nameSelector = "h2"
  override protected val websiteSelector = "div.maintextearea a[target=new][rel=nofollow]"
  override protected val mainSelector = "div.maintextearea"
}

object ProfesiaStaticCompScraper {
  def apply(html: String): StaticCompScraperResult = new ProfesiaStaticCompScraper().scrape(html)
}
