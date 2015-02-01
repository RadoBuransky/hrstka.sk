import controllers.{AppController, TechController}
import repositories.TechRepository
import repositories.mongoDb.MongoTechRepository
import services.TechService
import services.impl.TechServiceImpl

package object AppLoader {
  // Controllers
  lazy val appController = AppController()
  lazy val techController = TechController(techService)

  // Services
  lazy val techService: TechService = new TechServiceImpl(techRepository)

  // Repositories
  lazy val techRepository: TechRepository = new MongoTechRepository
}
