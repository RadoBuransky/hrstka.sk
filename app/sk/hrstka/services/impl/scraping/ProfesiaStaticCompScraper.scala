package sk.hrstka.services.impl.scraping

import java.net.URI

import org.jsoup.Jsoup

class ProfesiaStaticCompScraper extends BaseStaticCompScraper {
  override protected val nameSelectors = List("h2", "div.space-on-bottom > div > b:matches(.*Spoločnosť:.*) + a")
  override protected val websiteSelectors = List("div.maintextearea a[target=new][rel=nofollow]", "div.maintextearea a[target=_blank][rel=nofollow]")
  override protected val mainSelector = "div.maintextearea"
}

object ProfesiaStaticCompScraper {
  def apply(html: String): StaticCompScraperResult = new ProfesiaStaticCompScraper().scrape(html)
  def apply(url: URI): StaticCompScraperResult = apply(Jsoup.connect(url.toString).get().html())
}
