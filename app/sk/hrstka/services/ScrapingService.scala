package sk.hrstka.services

import com.google.inject.ImplementedBy
import sk.hrstka.models.domain.ScrapingResult
import sk.hrstka.services.impl.ProfesiaScrapingService

import scala.concurrent.Future

@ImplementedBy(classOf[ProfesiaScrapingService])
trait ScrapingService {
  def scrape: Future[ScrapingResult]
}
