package sk.hrstka.services.impl.scraping

import sk.hrstka.test.BaseSpec

class ProfesiaStaticCompScraperSpec extends BaseSpec {
  behavior of "scrape"

  it should "work for standard Profesia.sk page" in {
    val scrapedComp = ProfesiaStaticCompScraper(ProfesiaStaticCompScraperDataSpec.html)
    assertResult("Scheidt & Bachmann Slovensko")(scrapedComp.name)
  }
}