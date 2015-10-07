package sk.hrstka.services.impl.scraping

import sk.hrstka.test.BaseSpec

class ProfesiaStaticCompScraperSpec extends BaseSpec {
  behavior of "scrape"

  it should "work for standard Profesia.sk page" in {
    val scrapedComp = ProfesiaStaticCompScraper(ProfesiaStaticCompScraperDataSpec.html1)
    assertResult("Scheidt & Bachmann Slovensko")(scrapedComp.name)
    assertResult("http://www.scheidt-bachmann.sk")(scrapedComp.website.toString)
    assertResult(Set("java", "sql"))(scrapedComp.soTags)
  }

  it should "work for another standard Profesia.sk page" in {
    val scrapedComp = ProfesiaStaticCompScraper(ProfesiaStaticCompScraperDataSpec.html2)
    assertResult("NESS KDC")(scrapedComp.name)
    assertResult("http://www.nesskdc.sk")(scrapedComp.website.toString)
    assertResult(Set("oracle", "css", "directx", "testng", "processing", "beautifulsoup", "openid", "prototypejs", "requirejs", "jquery", "html", "hbase", "maps", "javascript", "visual-c++"))(scrapedComp.soTags)
  }
}