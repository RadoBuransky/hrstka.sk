package sk.hrstka.services.impl.scraping

import sk.hrstka.test.BaseSpec

class ProfesiaStaticCompScraperSpec extends BaseSpec {
  behavior of "scrape"

  it should "work for standard Profesia.sk page" in {
    val scrapedComp = ProfesiaStaticCompScraper(ProfesiaStaticCompScraperDataSpec.html1)
    assertResult("Scheidt & Bachmann Slovensko")(scrapedComp.name)
    assertResult("http://www.scheidt-bachmann.sk")(scrapedComp.website.toString)
    assertResult(Set("java", "sql"))(scrapedComp.soTags)
    assertResult(None)(scrapedComp.employeeCount)
  }

  it should "work for another standard Profesia.sk page" in {
    val scrapedComp = ProfesiaStaticCompScraper(ProfesiaStaticCompScraperDataSpec.html2)
    assertResult("NESS KDC")(scrapedComp.name)
    assertResult("http://www.nesskdc.sk")(scrapedComp.website.toString)
    assertResult(Set("oracle", "css", "directx", "testng", "processing", "beautifulsoup", "openid", "prototypejs", "requirejs", "jquery", "html", "hbase", "maps", "javascript", "visual-c++"))(scrapedComp.soTags)
    assertResult(Some("250-499"))(scrapedComp.employeeCount)
  }

  it should "work for yet another standard Profesia.sk page" in {
    val scrapedComp = ProfesiaStaticCompScraper(ProfesiaStaticCompScraperDataSpec.html3)
    assertResult("ANASOFT APR")(scrapedComp.name)
    assertResult("http://www.anasoft.sk")(scrapedComp.website.toString)
    assertResult(Set.empty)(scrapedComp.soTags)
    assertResult(Some("115"))(scrapedComp.employeeCount)
  }

  it should "work for yet yet another standard Profesia.sk page" in {
    val scrapedComp = ProfesiaStaticCompScraper(ProfesiaStaticCompScraperDataSpec.html4)
    assertResult("Miba Sinter Slovakia")(scrapedComp.name)
    assertResult("http://www.miba.com/")(scrapedComp.website.toString)
    assertResult(Set("soap"))(scrapedComp.soTags)
    assertResult(None)(scrapedComp.employeeCount)
  }
}