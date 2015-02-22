import controllers.{AppController, CompController, TechController}
import repositories.mongoDb.{MongoCompRepository, MongoCompTechRepository, MongoTechRepository, MongoTechVoteRepository}
import repositories.{CompRepository, CompTechRepository, TechRepository, TechVoteRepository}
import services.impl.{CompServiceImpl, TechServiceImpl}
import services.{CompService, TechService}

package object AppLoader {
  // Controllers
  lazy val appController = AppController()
  lazy val techController = TechController(techService)
  lazy val compController = CompController(compService, techService)

  // Services
  lazy val techService: TechService = new TechServiceImpl(techRepository, techVoteRepository)
  lazy val compService: CompService = new CompServiceImpl(compRepository, compTechRepository, techRepository)

  // Repositories
  lazy val techRepository: TechRepository = new MongoTechRepository
  lazy val techVoteRepository: TechVoteRepository = new MongoTechVoteRepository
  lazy val compRepository: CompRepository = new MongoCompRepository
  lazy val compTechRepository: CompTechRepository = new MongoCompTechRepository
}
