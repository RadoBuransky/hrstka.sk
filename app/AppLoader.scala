import controllers.{AppController, CompController, TechController}
import repositories.mongoDb.{MongoCompRepository, MongoTechRepository, MongoTechVoteLogRepository}
import repositories.{CompRepository, TechRepository, TechVoteLogRepository}
import services.impl.{CompServiceImpl, TechServiceImpl}
import services.{CompService, TechService}

package object AppLoader {
  // Controllers
  lazy val appController = AppController()
  lazy val techController = TechController(techService)
  lazy val compController = CompController(compService)

  // Services
  lazy val techService: TechService = new TechServiceImpl(techRepository, techVoteLogRepository)
  lazy val compService: CompService = new CompServiceImpl(compRepository)

  // Repositories
  lazy val techRepository: TechRepository = new MongoTechRepository
  lazy val techVoteLogRepository: TechVoteLogRepository = new MongoTechVoteLogRepository
  lazy val compRepository: CompRepository = new MongoCompRepository
}
