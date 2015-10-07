package sk.hrstka.services.impl.scraping

import sk.hrstka.test.BaseSpec

class GenericCompScraperSpec extends BaseSpec {
  behavior of "scrape"

  it should "work for standard Profesia.sk page" in {
    val scrapedComp = GenericCompScraper(ProfesiaCompScraperSpec.html)
    assertResult("Scheidt & Bachmann Slovensko")(scrapedComp.name)
  }
}